package kr.co.lee.howlstagram_kotlin.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import kr.co.lee.howlstagram_kotlin.utilites.RegisterState
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository,
) : BaseViewModel() {
    val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    val _userPassword = MutableLiveData<String>()
    val userPassword: LiveData<String> = _userPassword

    val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    val _userNickName = MutableLiveData<String>()
    val userNickName: LiveData<String> = _userNickName

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser> = _user

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun signUp(){
        viewModelScope.launch {
            val task = registerRepository.requestSignUp(
                userEmail.value.toString().trim(),
                userPassword.value.toString().trim()
            )

            task.addOnSuccessListener {
                _success.postValue(true)
                _user.postValue(it.user)
            }.addOnFailureListener {
                _success.postValue(false)
            }
        }
    }

    suspend fun insertUser(userItem: FirebaseUser): Task<FirebaseUser> {
        return registerRepository.insertUserInformation(userItem,
            userName.value.toString().trim(),
            userNickName.value.toString().trim()
        )
    }

    suspend fun insertUser2(userItem: FirebaseUser): RegisterState{
        val task = registerRepository.insertUserInformation(userItem,
            userName.value.toString().trim(),
            userNickName.value.toString().trim()
        )

        return when {
            task.isSuccessful -> RegisterState.Success
            else -> RegisterState.Failed
        }
    }
}