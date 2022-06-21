package kr.co.lee.howlstargram_kotlin.ui.follow

import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import kr.co.lee.howlstargram_kotlin.utilites.FOLLOW
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val followRepository: FollowRepository,
    @CurrentUserUid val currentUserUid: String,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val follow = savedStateHandle.get<Map<String, Boolean>>(FOLLOW)
        ?: throw IllegalStateException("There is no follow.")

    private val _uiState = MutableStateFlow<UiState<List<FavoriteDTO>>>(UiState.Loading)
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    init {
        viewModelScope.launch {
            followRepository.getAllFollows(follow)
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    private val _followDTOs = MutableLiveData<List<FavoriteDTO>>()
    val followDTOs: LiveData<List<FavoriteDTO>> = _followDTOs

    // 팔로우 요청
    suspend fun requestFollow(userUid: String, position: Int): Flow<UiState<Boolean>> {
        return withContext(viewModelScope.coroutineContext) {
            followRepository.saveThirdPerson(userUid)
            followRepository.saveMyAccount(userUid)
        }
    }
}