package kr.co.lee.howlstagram_kotlin.ui.follow

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import kr.co.lee.howlstagram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstagram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstagram_kotlin.utilites.FOLLOW
import kr.co.lee.howlstagram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val followRepository: FollowRepository,
    @CurrentUserUid val currentUserUid: String,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
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

    // 팔로우 요청
    suspend fun requestFollow(userUid: String): Flow<UiState<Boolean>> {
        return withContext(viewModelScope.coroutineContext) {
            followRepository.saveThirdPerson(userUid)
            followRepository.saveMyAccount(userUid)
        }
    }
}
