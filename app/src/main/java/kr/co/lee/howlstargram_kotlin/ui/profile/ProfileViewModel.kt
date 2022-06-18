package kr.co.lee.howlstargram_kotlin.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _profileUrl = MutableLiveData<String?>()
    val profileUrl: LiveData<String?> = _profileUrl

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _userNickName = MutableLiveData<String>()
    val userNickName: LiveData<String> = _userNickName

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    init {
        viewModelScope.launch {
            _uid.postValue(auth.currentUser?.uid)
        }
    }

    fun getIntentData(profileUrl: String?, userName: String?, userNickName: String?) {
        viewModelScope.launch {
            if(profileUrl != null && userName != null && userNickName != null) {
                _profileUrl.postValue(profileUrl!!)
                _userName.postValue(userName!!)
                _userNickName.postValue(userNickName!!)
            }
        }
    }

    fun updateProfile(profileUrl: String?) {
        _profileUrl.postValue(profileUrl)
    }

    fun updateUser(userName: String, userNickName: String): Job {
        val job = viewModelScope.launch {
            requestUpdateUser(uid.value!!, userName, userNickName)
        }

        return job
    }

    private suspend fun requestUpdateUser(uid: String, userName: String, userNickName: String) {
        withContext(ioDispatcher) {
            val tsDoc = fireStore.collection("users").document(uid)
            fireStore.runTransaction { transaction ->
                val userDTO = transaction.get(tsDoc).toObject(UserDTO::class.java)
                userDTO?.userName = userName
                userDTO?.userNickName = userNickName
                transaction.set(tsDoc, userDTO!!)
            }
        }
    }
}