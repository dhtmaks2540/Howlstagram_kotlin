package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(application: Application): AndroidViewModel(application) {
    private val _imageList = MutableLiveData<List<GalleryImage>>()
    private val _currentSelectedImage = MutableLiveData<String>()

    val imageList: LiveData<List<GalleryImage>> = _imageList
    val currentSelectedImage = _currentSelectedImage

    fun setCurrentImage(image: String) {
        _currentSelectedImage.postValue(image)
    }

    fun loadImages() {
        viewModelScope.launch {
            val imageList = queryImages()
            _imageList.postValue(imageList)
        }
    }

    private suspend fun queryImages(): List<GalleryImage> {
        val imageList = mutableListOf<GalleryImage>()

        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )

            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null, null, null
            )?.use { cursor ->
                // Cache Column indices
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val date = cursor.getLong(dateColumn)
                    val contentUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )
                    val galleryImage = GalleryImage(contentUri.toString(), name, date)
                    if (!imageList.contains(galleryImage)) {
                        imageList.add(galleryImage)
                    }
                }
            }
        }

        return imageList
    }
}