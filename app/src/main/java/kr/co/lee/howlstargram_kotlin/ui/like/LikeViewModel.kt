package kr.co.lee.howlstargram_kotlin.ui.like

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Comment
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import kr.co.lee.howlstargram_kotlin.utilites.FAVORITES
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    @CurrentUserUid val currentUserUid: String,
    private val likeRepository: LikeRepository,
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val favorites = savedStateHandle.get<Map<String, Boolean>>(FAVORITES)
        ?: throw IllegalStateException("There is no value of the champion id.")

    private val _favoriteDTOs = MutableLiveData<List<FavoriteDTO>>()
    val favoriteDTOs: LiveData<List<FavoriteDTO>> = _favoriteDTOs

    private val _uiState = MutableStateFlow<UiState<List<FavoriteDTO>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<FavoriteDTO>>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    init {
        viewModelScope.launch {
            likeRepository.getAllFavorites(favorites)
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    // 팔로우 요청
    suspend fun requestFollow(userUid: String, position: Int): Flow<UiState<Boolean>> {
        return withContext(viewModelScope.coroutineContext) {
            likeRepository.saveThirdPerson(userUid)
            likeRepository.saveMyAccount(userUid)
        }
    }

    // 팔로우 요청에 대한 내 팔로잉 정보 수정
    private suspend fun saveMyAccount(userUid: String, uid: String, position: Int) {
        withContext(ioDispatcher) {
            val tsDocFollowing = fireStore.collection("users").document(uid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollowing).toObject(UserDTO::class.java)
                if (followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followingCount = 1
                    followDTOItem.followings[userUid] = true

                    transaction.set(tsDocFollowing, followDTOItem)
                    return@runTransaction
                }

                if (followDTOItem.followings.containsKey(userUid)) {
                    // remove
                    followDTOItem.followingCount = followDTOItem.followingCount - 1
                    followDTOItem.followings.remove(userUid)
                } else {
                    // add
                    followDTOItem.followingCount = followDTOItem.followingCount + 1
                    followDTOItem.followings[userUid] = true
                }

                val isFollow = followDTOItem.followings.containsKey(userUid)

                transaction.set(tsDocFollowing, followDTOItem)
//                val newFavoriteDTOs = favoriteDTOs.value?.toMutableList()
//                newFavoriteDTOs?.set(position, newFavoriteDTOs[position].copy(isFollow = isFollow))
//                _favoriteDTOs.postValue(newFavoriteDTOs!!)
            }
        }
    }

    // 팔로우 요청에 대한 상대방 팔로워 정보 수정
    private suspend fun saveThirdPerson(userUid: String, uid: String) {
        withContext(ioDispatcher) {
            val tsDocFollower = fireStore.collection("users").document(userUid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollower).toObject(UserDTO::class.java)
                if (followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followerCount = 1
                    followDTOItem.followers[uid] = true

                    transaction.set(tsDocFollower, followDTOItem)
                    return@runTransaction
                }

                if (followDTOItem.followers.containsKey(uid)) {
                    // cancel
                    followDTOItem.followerCount = followDTOItem.followerCount - 1
                    followDTOItem.followers.remove(uid)
                } else {
                    // add
                    followDTOItem.followerCount = followDTOItem.followerCount + 1
                    followDTOItem.followers[uid] = true
                }

                transaction.set(tsDocFollower, followDTOItem)
            }

        }
    }
}