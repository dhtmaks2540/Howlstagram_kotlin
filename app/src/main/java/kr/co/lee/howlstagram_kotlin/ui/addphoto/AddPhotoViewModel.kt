package kr.co.lee.howlstagram_kotlin.ui.addphoto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import kr.co.lee.howlstagram_kotlin.model.ContentDTO
import kr.co.lee.howlstagram_kotlin.utilites.URI
import javax.inject.Inject

@HiltViewModel
class AddPhotoViewModel @Inject constructor(
    private val addPhotoRepository: AddPhotoRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    val uri = savedStateHandle.get<String>(URI)
        ?: throw IllegalStateException("There is no value of uri")

    private val _contentDTO = MutableLiveData<ContentDTO>()
    val contentDTO: LiveData<ContentDTO> = _contentDTO

    val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _isUpload = MutableLiveData<Boolean>()
    val isUpload: LiveData<Boolean> = _isUpload

    // 이미지 업로드
    suspend fun contentUploadImage(): Task<Void> {
        return addPhotoRepository.requestContentUpload(uri, description.value!!)
    }
}
