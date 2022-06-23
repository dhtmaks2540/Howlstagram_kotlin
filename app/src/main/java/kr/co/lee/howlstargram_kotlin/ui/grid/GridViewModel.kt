package kr.co.lee.howlstargram_kotlin.ui.grid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class GridViewModel @Inject constructor(
    private val gridRepository: GridRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Content>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Content>>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    init {
        viewModelScope.launch {
            gridRepository.getAllContents()
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun refresh(): Job {
        val job = viewModelScope.launch {
            gridRepository.getAllContents()
                .collect { state ->
                    _uiState.value = state
                }
        }

        return job
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}