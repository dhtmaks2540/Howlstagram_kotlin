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
    suspend fun requestFollow(userUid: String): Flow<UiState<Boolean>> {
        return withContext(viewModelScope.coroutineContext) {
            likeRepository.saveThirdPerson(userUid)
            likeRepository.saveMyAccount(userUid)
        }
    }
}