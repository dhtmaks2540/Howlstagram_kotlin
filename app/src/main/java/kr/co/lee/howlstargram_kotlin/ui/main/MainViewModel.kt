package kr.co.lee.howlstargram_kotlin.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth) : ViewModel() {
    lateinit var navController: LiveData<NavController>
    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    init {
        viewModelScope.launch {
            _uid.postValue(firebaseAuth.currentUser?.uid)
        }
    }

    fun setController(controller: LiveData<NavController>) {
        viewModelScope.launch {
            navController = controller
        }
    }
}