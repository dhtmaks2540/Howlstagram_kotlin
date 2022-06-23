package kr.co.lee.howlstargram_kotlin.utilites

sealed class UiState<out T> {
    object Waiting : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Failed<T>(val message: String) : UiState<T>()
}

fun <T> UiState<T>.successOrNull(): T? = if (this is UiState.Success<T>) {
    data
} else {
    null
}

fun <T> UiState<T>.throwableOrNull(): String? = if (this is UiState.Failed) {
    message
} else {
    null
}