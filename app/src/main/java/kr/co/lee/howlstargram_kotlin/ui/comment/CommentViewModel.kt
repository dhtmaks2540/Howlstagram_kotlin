package kr.co.lee.howlstargram_kotlin.ui.comment

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.model.Comment
import kr.co.lee.howlstargram_kotlin.model.CommentDTO
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    @CurrentUserUid private val currentUserUid: String,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val _commentContent = MutableLiveData<String>()
    val commentContent: LiveData<String> = _commentContent

    val content: Content = savedStateHandle.get<Content>(CONTENT)
        ?: throw IllegalStateException("There is no value of the champion id.")

    private val _uiState = MutableStateFlow<UiState<List<Comment>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Comment>>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    init {
        viewModelScope.launch {
            val result = commentRepository.loadMyInfo()
            _user.postValue(result)

            commentRepository.getAllComments(content)
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun refresh(): Job {
        val job = viewModelScope.launch {
            commentRepository.getAllComments(content)
                .collect { state ->
                    _uiState.value = state
                }
        }

        return job
    }

    suspend fun addComment(): Flow<UiState<Comment>> {
        return withContext(viewModelScope.coroutineContext) {
            val commentDTO = CommentDTO(
                uid = currentUserUid,
                userNickName = user.value?.userDTO?.userNickName,
                comment = commentContent.value,
                timestamp = System.currentTimeMillis()
            )
            val commentState = commentRepository.addComment(
                Comment(commentDTO = commentDTO, profileUrl = user.value?.profileUrl),
                content.contentUid!!
            )

            commentState
        }
    }
}
