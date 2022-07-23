package kr.co.lee.howlstargram_kotlin.ui.research

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.ResearchContent
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_DTO
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_UID
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class ResearchViewModel @Inject constructor(
    private val researchRepository: ResearchRepository,
    @CurrentUserUid val currentUserUid: String,
    stateHandle: SavedStateHandle,
): ViewModel() {
    private val contentDTO = stateHandle.get<ContentDTO>(CONTENT_DTO)
        ?: throw IllegalStateException("There is no value of contentDTO.")
    private val contentUid = stateHandle.get<String>(CONTENT_UID)
        ?: throw IllegalStateException("There is no value of contentUid.")

    private val _userAndContent = MutableLiveData<ResearchContent>()
    val userAndContent: LiveData<ResearchContent> = _userAndContent

    init {
        viewModelScope.launch {
            val result = researchRepository.getUserAndContent(contentDTO, contentUid)
            _userAndContent.postValue(result)
        }
    }

    fun favoriteEvent() {
        viewModelScope.launch {
            if(userAndContent.value?.content?.contentUid != null) {
                val task = researchRepository.requestFavoriteEvent(userAndContent.value?.content?.contentUid!!)
                task.addOnSuccessListener { result ->
                    val newContent = _userAndContent.value?.content?.copy(contentDTO = result)
                    newContent?.let {
                        _userAndContent.postValue(_userAndContent.value?.copy(content = it))
                    }
                }
            }
        }
    }

    // 팔로우 요청
    fun requestFollow() {
        viewModelScope.launch {
            coroutineScope {
                val job = researchRepository.saveMyAccount(userAndContent.value?.content?.contentDTO?.uid!!)
                researchRepository.saveThirdPerson(userAndContent.value?.content?.contentDTO?.uid!!)

                job.addOnSuccessListener { result ->
                    _userAndContent.postValue(_userAndContent.value?.copy(isFollow = result))
                }
            }
        }
    }
}