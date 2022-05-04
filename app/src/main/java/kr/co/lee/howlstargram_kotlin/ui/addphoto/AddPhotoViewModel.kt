package kr.co.lee.howlstargram_kotlin.ui.addphoto

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddPhotoViewModel @Inject constructor(): ViewModel() {
    private val _contentDTO = MutableLiveData<ContentDTO>()
    val _description = MutableLiveData<String>()
    private val _uri = MutableLiveData<String>()

    @Inject
    lateinit var storage: FirebaseStorage
    @Inject
    lateinit var auth: FirebaseAuth

    val contentDTO: LiveData<ContentDTO> = _contentDTO
    val description: LiveData<String> = _description
    val uri: LiveData<String> = _uri

    fun contentUploadImage() {
        viewModelScope.launch {
            uploadImage()
        }
    }

    private suspend fun uploadImage() {
        withContext(Dispatchers.IO) {
            // 파일 이름 생성
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "IMAGE_${timeStamp}_.png"

            val storageRef = storage.reference.child("images").child(imageFileName)

            Uri.parse(uri.value).let {
                // Callback Method
                storageRef.putFile(it).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        setContentDTO(uri.toString(), auth.currentUser?.uid,
                            auth.currentUser?.email, description.value.toString())
                    }
                }
            }
        }

    }

    fun setUri(uri: String) {
        viewModelScope.launch {
            _uri.postValue(uri)
        }
    }

    private fun setContentDTO(imageUrl: String?, uid: String?, userId: String?, explain: String?) {
        viewModelScope.launch {
            val contentDTO = ContentDTO()

            // Insert downloadUrl of image
            contentDTO.imageUrl = imageUrl
            // Insert uid of user
            contentDTO.uid = uid
            // Insert UserId
            contentDTO.userId = userId
            // Insert explain of content
            contentDTO.explain = explain
            // Insert TimeStamp
            contentDTO.timestamp = System.currentTimeMillis()

            _contentDTO.postValue(contentDTO)
        }
    }
}