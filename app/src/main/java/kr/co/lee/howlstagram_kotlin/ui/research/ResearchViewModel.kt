package kr.co.lee.howlstagram_kotlin.ui.research

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import kr.co.lee.howlstagram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstagram_kotlin.model.ContentDTO
import kr.co.lee.howlstagram_kotlin.model.ResearchContent
import kr.co.lee.howlstagram_kotlin.utilites.CONTENT_DTO
import kr.co.lee.howlstagram_kotlin.utilites.CONTENT_UID
import javax.inject.Inject

@HiltViewModel
class ResearchViewModel @Inject constructor(
    private val researchRepository: ResearchRepository,
    @CurrentUserUid val currentUserUid: String,
    stateHandle: SavedStateHandle,
): BaseViewModel() {
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