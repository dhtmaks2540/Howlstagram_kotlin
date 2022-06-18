package kr.co.lee.howlstargram_kotlin.ui.addphoto

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddPhotoViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _contentDTO = MutableLiveData<ContentDTO>()
    val contentDTO: LiveData<ContentDTO> = _contentDTO

    val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _uri = MutableLiveData<String>()
    val uri: LiveData<String> = _uri

    private val _isUpload = MutableLiveData<Boolean>()
    val isUpload: LiveData<Boolean> = _isUpload

    // 이미지 업로드
    suspend fun contentUploadImage(): Job {
        val job = viewModelScope.launch {
            val uri = async(ioDispatcher) {
                uploadImage()
            }

            val contentDTOItem = async(ioDispatcher) {
                setContentDTO(
                    uri.await(),
                    auth.currentUser?.uid,
                    auth.currentUser?.email,
                    description.value.toString()
                )
            }

            launch(ioDispatcher) {
                insertImage(contentDTOItem.await())
            }
        }

        return job
    }

    // ContentDTO를 데이터베이스에 삽입
    private suspend fun insertImage(contentDTO: ContentDTO) {
        fireStore.collection("images").document().set(contentDTO)
    }

    // 이미지 추가 및 이미지 Uri 반환
    private suspend fun uploadImage(): String {
        // 이미지 이름 생성
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_${timeStamp}_.png"
        // 이미지 Uri
        val uriValue = Uri.parse(uri.value)
        // Storage 위치 지정
        val storageRef = storage.reference.child("images").child(imageFileName)

        // 이미지 추가
        return storageRef.putFile(uriValue)
            .await()
            .storage
            .downloadUrl
            .await()
            .toString()

//            Uri.parse(uri.value).let {
//                // Callback Method
//                storageRef.putFile(it).addOnSuccessListener {
//                    storageRef.downloadUrl.addOnSuccessListener { uri ->
//                        setContentDTO(uri.toString(), auth.currentUser?.uid,
//                            auth.currentUser?.email, description.value.toString())
    }

    fun setUri(uri: String) {
        viewModelScope.launch {
            _uri.postValue(uri)
        }
    }

    // ContentDTO 생성
    private suspend fun setContentDTO(imageUrl: String?, uid: String?, userId: String?, explain: String?): ContentDTO {
        val userSnapShot = fireStore.collection("users").document(uid!!).get().await()
        val userDTOItem = userSnapShot.toObject(UserDTO::class.java)
        val contentDTO = ContentDTO()

        // Insert downloadUrl of image
        contentDTO.imageUrl = imageUrl
        // Insert uid of user
        contentDTO.uid = uid
        // Insert UserId
        contentDTO.userId = userId
        // Insert UserNickName
        contentDTO.userNickName = userDTOItem?.userNickName
        // Insert UserName
        contentDTO.userName = userDTOItem?.userName
        // Insert explain of content
        contentDTO.explain = explain
        // Insert TimeStamp
        contentDTO.timestamp = System.currentTimeMillis()

        return contentDTO
    }
}
