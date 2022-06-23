package kr.co.lee.howlstargram_kotlin.ui.profile

import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.utilites.PROFILE_URL
import kr.co.lee.howlstargram_kotlin.utilites.USER_NAME
import kr.co.lee.howlstargram_kotlin.utilites.USER_NICKNAME
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _profileUrl = MutableLiveData(
        savedStateHandle.get<String>(PROFILE_URL)
            ?: throw IllegalStateException("There is no value of profileUrl.")
    )
    val profileUrl: LiveData<String> = _profileUrl

    val userName = savedStateHandle.get<String>(USER_NAME)
        ?: throw IllegalStateException("There is no value of userName.")

    val userNickName = savedStateHandle.get<String>(USER_NICKNAME)
        ?: throw IllegalStateException("There is no value of userNickName.")

    fun updateProfile(profileUrl: String?) {
        viewModelScope.launch {
            _profileUrl.postValue(profileUrl)
        }
    }

    suspend fun updateUser(userName: String, userNickName: String): Task<Transaction> {
        return profileRepository.requestUpdateUser(userName, userNickName)
    }
}
