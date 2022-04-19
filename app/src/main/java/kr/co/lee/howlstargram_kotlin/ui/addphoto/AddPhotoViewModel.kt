package kr.co.lee.howlstargram_kotlin.ui.addphoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import javax.inject.Inject

@HiltViewModel
class AddPhotoViewModel @Inject constructor(): ViewModel() {
    private val _contentDTO = MutableLiveData<ContentDTO>()
    val _description = MutableLiveData<String>()

    val contentDTO: LiveData<ContentDTO>
        get() = _contentDTO

    val description: LiveData<String>
        get() = _description

    fun setContentDTO(imageUrl: String?, uid: String?, userId: String?, explain: String?) {
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
    }
}