package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

class GalleryRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth,
    private val fireStorage: FirebaseStorage,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid private val currentUserUid: String,
) {
    // 갤러리 이미지 불러오기
    fun getAllImages() = flow<UiState<List<GalleryImage>>> {
        emit(UiState.Loading)
        val imageList = mutableListOf<GalleryImage>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        context.contentResolver.query(
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
        emit(UiState.Success(imageList))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)

    // 프로필 업로드
    suspend fun insertProfile(currentImageUri: String?): Task<Uri> {
        return withContext(Dispatchers.IO) {
            val storageRef = fireStorage.reference.child("userProfileImages").child(currentUserUid)
            storageRef.putFile(Uri.parse(currentImageUri))
                .continueWithTask { storageRef.downloadUrl }
                .addOnSuccessListener { uri ->
                    val map = hashMapOf("image" to uri.toString())
                    fireStore.collection("profileImages").document(currentUserUid).set(map)
                }
        }
    }
}