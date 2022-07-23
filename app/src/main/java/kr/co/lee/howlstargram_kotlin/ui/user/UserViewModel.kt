package kr.co.lee.howlstargram_kotlin.ui.user

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.model.UserAndContent
import kr.co.lee.howlstargram_kotlin.utilites.DESTINATION_UID
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
    @CurrentUserUid val currentUserId: String,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _isMyProfile = MutableLiveData<Boolean>()
    val isMyProfile: LiveData<Boolean> = _isMyProfile

    private val _uid = if (savedStateHandle.get<String>(DESTINATION_UID) == "") {
        viewModelScope.launch {
            _isMyProfile.postValue(false)
        }
        MutableLiveData(currentUserId)
    } else {
        viewModelScope.launch {
            _isMyProfile.postValue(true)
        }
        MutableLiveData(savedStateHandle.get(DESTINATION_UID))
    }

    val uid: LiveData<String> = _uid

    private val _userAndContent = MutableLiveData<UserAndContent>()
    val userAndContent: LiveData<UserAndContent> = _userAndContent

    init {
        refresh()
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun refresh(): Job {
        val job = viewModelScope.launch {
            uid.value?.let {
                val result = userRepository.getUserAndContentDTOs(it)
                _userAndContent.postValue(result)
            }
        }

        return job
    }

    // 팔로우 요청
    fun requestFollow() {
        viewModelScope.launch {
            coroutineScope {
                userRepository.saveMyAccount(uid.value!!)
                val task = userRepository.saveThirdPerson(uid.value!!)
                task.addOnSuccessListener { result ->
                    val user = _userAndContent.value?.user?.copy(userDTO = result)
                    user?.let {
                        _userAndContent.postValue(_userAndContent.value?.copy(user = it))
                    }
                }
            }
        }
    }

    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
        }
    }
}