package kr.co.lee.howlstagram_kotlin.ui.gallery

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import kr.co.lee.howlstagram_kotlin.model.GalleryImage
import kr.co.lee.howlstagram_kotlin.utilites.IMAGE_TYPE
import kr.co.lee.howlstagram_kotlin.utilites.UiState
import java.io.Serializable
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryRepository: GalleryRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<GalleryImage>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<GalleryImage>>> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState.Loading
    )

    val imageType = savedStateHandle.get<Serializable>(IMAGE_TYPE)

    private val _currentSelectedImage = MutableLiveData<String>()
    val currentSelectedImage = _currentSelectedImage

    fun setCurrentImage(image: String) {
        viewModelScope.launch {
            _currentSelectedImage.postValue(image)
        }
    }

    fun loadImages() {
        viewModelScope.launch {
            galleryRepository.getAllImages().collect { state ->
                _uiState.value = state
            }
        }
    }

    suspend fun addProfile(): Task<Uri> {
        return galleryRepository.insertProfile(currentSelectedImage.value)
    }
}
