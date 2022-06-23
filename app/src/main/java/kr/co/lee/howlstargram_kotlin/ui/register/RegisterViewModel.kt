package kr.co.lee.howlstargram_kotlin.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository,
) : ViewModel() {
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
}