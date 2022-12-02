package kr.co.lee.howlstagram_kotlin.ui.detail

import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import kr.co.lee.howlstagram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstagram_kotlin.model.Content
import kr.co.lee.howlstagram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val detailRepository: DetailRepository,
    @CurrentUserUid val currentUserUid: String,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Content>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Content>>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            detailRepository.getAllContents()
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun refresh(): Job {
        val job = viewModelScope.launch {
            detailRepository.getAllContents()
                .collect { state ->
                    _uiState.value = state
                }
        }

        return job
    }

    suspend fun setFavoriteEvent(contentUid: String): Task<Transaction> {
        return detailRepository.favoriteEvent(contentUid)
    }
}
