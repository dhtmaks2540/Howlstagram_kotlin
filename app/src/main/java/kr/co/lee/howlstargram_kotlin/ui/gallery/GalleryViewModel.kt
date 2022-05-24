package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import kr.co.lee.howlstargram_kotlin.utilites.ImageType
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    application: Application, private val fireStore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth, private val firebaseStorage: FirebaseStorage): AndroidViewModel(application) {
    private val _imageList = MutableLiveData<List<GalleryImage>>()
    private val _currentSelectedImage = MutableLiveData<String>()
    private val _imageType = MutableLiveData<ImageType>()
    private val _uid = MutableLiveData<String>()

    val imageList: LiveData<List<GalleryImage>> = _imageList
    val currentSelectedImage = _currentSelectedImage
    val imageType: LiveData<ImageType> = _imageType
    val uid: LiveData<String> = _uid

    init {
        viewModelScope.launch {
            _uid.postValue(firebaseAuth.currentUser?.uid)
        }
    }

    fun setImageType(imageType: ImageType) {
        viewModelScope.launch {
            _imageType.postValue(imageType)
        }
    }

    fun setCurrentImage(image: String) {
        viewModelScope.launch {
            _currentSelectedImage.postValue(image)
        }
    }

    fun loadImages() {
        viewModelScope.launch {
            val imageList = queryImages()
            _imageList.postValue(imageList)
        }
    }

    fun addProfile() {
        viewModelScope.launch {
            insertProfile()
        }
    }

    private suspend fun insertProfile() {
        withContext(Dispatchers.IO) {
            val storageRef = firebaseStorage.reference.child("userProfileImages").child(uid.value!!)
            storageRef.putFile(Uri.parse(currentSelectedImage.value)).continueWithTask { storageRef.downloadUrl }
                .addOnSuccessListener { uri ->
                val map = hashMapOf("image" to uri.toString())
                fireStore.collection("profileImages").document(uid.value!!).set(map)
            }
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