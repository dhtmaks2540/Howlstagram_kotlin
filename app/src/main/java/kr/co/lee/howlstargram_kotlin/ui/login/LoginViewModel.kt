package kr.co.lee.howlstargram_kotlin.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    val _email = MutableLiveData<String>()
    val _password = MutableLiveData<String>()

    val email: LiveData<String>
        get() = _email
    val password: LiveData<String>
        get() = _password
}