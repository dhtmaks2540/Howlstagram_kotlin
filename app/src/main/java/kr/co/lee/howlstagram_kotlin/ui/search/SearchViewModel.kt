package kr.co.lee.howlstagram_kotlin.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import kr.co.lee.howlstagram_kotlin.model.User
import kr.co.lee.howlstagram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
): BaseViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Waiting)
    val uiState: StateFlow<UiState<List<User>>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Waiting
    )

    val _textInput = MutableLiveData<String>()
    val textInput: LiveData<String> = _textInput

    fun findUsers(text: String): Job {
        val job = viewModelScope.launch {
            searchRepository.getAllUsers(text)
                .collect { state ->
                    _uiState.value = state
                }
        }

        return job
    }
}