package kr.co.lee.howlstargram_kotlin.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    val _email = MutableLiveData<String>()
    val _password = MutableLiveData<String>()

    val email: LiveData<String>
        get() = _email
    val password: LiveData<String>
        get() = _password
}