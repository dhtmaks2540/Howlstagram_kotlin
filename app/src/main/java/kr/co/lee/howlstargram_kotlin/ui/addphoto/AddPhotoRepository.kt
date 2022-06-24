package kr.co.lee.howlstargram_kotlin.ui.addphoto

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserEmail
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class AddPhotoRepository @Inject constructor(
    private val fireStorage: FirebaseStorage,
    private val fireStore: FirebaseFirestore,
    @CurrentUserUid private val currentUserUid: String,
    @CurrentUserEmail private val currentUserEmail: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    // 글 업로드 요청
    suspend fun requestContentUpload(uri: String, explain: String): Task<Void> {
        return withContext(ioDispatcher) {
            // 이미지 이름 생성
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "IMAGE_${timeStamp}_.png"
            // 이미지 Uri
            val uriValue = Uri.parse(uri)
            // Storage 위치 지정
            val storageRef = fireStorage.reference.child("images").child(imageFileName)

            // 이미지 추가
            val imageUri =
                storageRef.putFile(uriValue).await().storage.downloadUrl.await().toString()

            val userSnapShot = fireStore.collection("users").document(currentUserUid).get().await()
            val userDTOItem = userSnapShot.toObject(UserDTO::class.java)
            val contentDTO = ContentDTO()

            // Insert downloadUrl of image
            contentDTO.imageUrl = imageUri
            // Insert uid of user
            contentDTO.uid = currentUserUid
            // Insert UserId
            contentDTO.userId = currentUserEmail
            // Insert UserNickName
            contentDTO.userNickName = userDTOItem?.userNickName
            // Insert UserName
            contentDTO.userName = userDTOItem?.userName
            // Insert explain of content
            contentDTO.explain = explain
            // Insert TimeStamp
            contentDTO.timestamp = System.currentTimeMillis()

            fireStore.collection("images").document().set(contentDTO)
        }
    }
}
