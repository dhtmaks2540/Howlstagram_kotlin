package kr.co.lee.howlstargram_kotlin.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(): ViewModel() {
    private val _imageList = MutableLiveData<List<GalleryImage>>()
    private val _currentSelectedImage = MutableLiveData<String>()

    val imageList: LiveData<List<GalleryImage>> = _imageList
    val currentSelectedImage = _currentSelectedImage

    fun setImageList(imageList: List<GalleryImage>) {
        _imageList.postValue(imageList)
    }

    fun setCurrentImage(image: String) {
        _currentSelectedImage.postValue(image)
    }
}