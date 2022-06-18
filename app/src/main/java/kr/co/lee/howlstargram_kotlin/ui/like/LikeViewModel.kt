package kr.co.lee.howlstargram_kotlin.ui.like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid val currentUserUid: String,
): ViewModel() {

    private val _favorites = MutableLiveData<Map<String, Boolean>>()
    val favorites: LiveData<Map<String, Boolean>> = _favorites

    private val _favoriteDTOs = MutableLiveData<List<FavoriteDTO>>()
    val favoriteDTOs: LiveData<List<FavoriteDTO>> = _favoriteDTOs

    fun setFavorites(favorites: Map<String, Boolean>?) {
        favorites?.let {
            viewModelScope.launch {
                _favorites.postValue(it)
            }
        }
    }

    fun loadFavorite(favorites: Map<String, Boolean>) {
        viewModelScope.launch {
            val result = loadFavorites(favorites)
            _favoriteDTOs.postValue(result)
        }
    }

    // 팔로우 요청
    fun requestFollow(userUid: String, position: Int): Job {
        val job = viewModelScope.launch {
            coroutineScope {
                saveMyAccount(userUid, currentUserUid, position)
                saveThirdPerson(userUid, currentUserUid)
            }
        }

        return job
    }

    private suspend fun loadFavorites(favorites: Map<String, Boolean>): ArrayList<FavoriteDTO> {
        return withContext(ioDispatcher) {
            val favoriteDTOItems = ArrayList<FavoriteDTO>()
            favorites.forEach { (userId, _) ->
                val userSnapShot = fireStore.collection("users").document(userId).get().await()
                val profileSnapShot = fireStore.collection("profileImages").document(userId).get().await()
                val isFollow = (userSnapShot.data?.get("followers") as Map<*, *>).containsKey(currentUserUid)

                val userItem = userSnapShot.toObject(UserDTO::class.java)

                userItem?.let {
                    val favoriteDTO = FavoriteDTO(userUid = userId,
                        profileUrl = profileSnapShot.data?.get("image").toString(),
                        userName = it.userName,
                        userNickName = it.userNickName,
                        isFollow = isFollow)

                    favoriteDTOItems.add(favoriteDTO)
                }
            }

            favoriteDTOItems
        }
    }

    // 팔로우 요청에 대한 내 팔로잉 정보 수정
    private suspend fun saveMyAccount(userUid: String, uid: String, position: Int) {
        withContext(ioDispatcher) {
            val tsDocFollowing = fireStore.collection("users").document(uid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollowing).toObject(UserDTO::class.java)
                if(followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followingCount = 1
                    followDTOItem.followings[userUid] = true

                    transaction.set(tsDocFollowing, followDTOItem)
                    return@runTransaction
                }

                if(followDTOItem.followings.containsKey(userUid)) {
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
                val newFavoriteDTOs = favoriteDTOs.value?.toMutableList()
                newFavoriteDTOs?.set(position, newFavoriteDTOs[position].copy(isFollow = isFollow))
                _favoriteDTOs.postValue(newFavoriteDTOs!!)
            }
        }
    }

    // 팔로우 요청에 대한 상대방 팔로워 정보 수정
    private suspend fun saveThirdPerson(userUid: String, uid: String) {
        withContext(ioDispatcher) {
            val tsDocFollower = fireStore.collection("users").document(userUid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollower).toObject(UserDTO::class.java)
                if(followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followerCount = 1
                    followDTOItem.followers[uid] = true

                    transaction.set(tsDocFollower, followDTOItem)
                    return@runTransaction
                }

                if(followDTOItem.followers.containsKey(uid)) {
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