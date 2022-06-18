package kr.co.lee.howlstargram_kotlin.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher): ViewModel() {
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

    fun signUp() {
        viewModelScope.launch {
            requestSignUp()
        }
    }

    fun insertUser(userItem: FirebaseUser) {
        viewModelScope.launch {
            insertUserInformation(userItem)
        }
    }

    private suspend fun requestSignUp() {
        withContext(ioDispatcher) {
            auth.createUserWithEmailAndPassword(
                userEmail.value.toString().trim(),
                userPassword.value.toString().trim()
            ).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _success.postValue(true)
                    _user.postValue(task.result.user)
                } else {
                    _success.postValue(false)
                }
            }.addOnFailureListener {
                println("FAILURE!!! : ${it.message}")
            }
        }
    }

    // 유저 정보 삽입
    private suspend fun insertUserInformation(userItem: FirebaseUser) {
        withContext(ioDispatcher) {
            val tsDocUser = fireStore.collection("users").document(userItem.uid)
            fireStore.runTransaction { transaction ->
                val item = transaction.get(tsDocUser).toObject(UserDTO::class.java)
                if(item == null) {
                    val userDTO = UserDTO(userEmail = userItem.email.toString(),
                        userName = userName.value.toString(),
                        userNickName = userNickName.value.toString())
                    transaction.set(tsDocUser, userDTO)
                }
            }
        }
    }
}