package kr.co.lee.howlstagram_kotlin.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.lee.howlstagram_kotlin.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    suspend fun signInEmail(): Task<AuthResult> {
        return loginRepository.requestSignInEmail(
            email.value.toString().trim(),
            password.value.toString().trim()
        )
    }
}