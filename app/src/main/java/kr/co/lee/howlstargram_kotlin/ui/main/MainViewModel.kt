package kr.co.lee.howlstargram_kotlin.ui.main

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.utilites.FragmentType
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth): ViewModel() {
    private val _currentFragmentType = MutableLiveData(FragmentType.FRAGMENT1)
    val currentFragmentType: LiveData<FragmentType> = _currentFragmentType

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    init {
        viewModelScope.launch {
            _uid.postValue(firebaseAuth.currentUser?.uid)
        }
    }

    // FragmentType에 따라 currentPageType 변경
    // BindingAdapter에 의해서 호출 될 메소드
    fun setCurrentFragment(item: MenuItem): Boolean {
        val menuItemId = item.itemId
        val pageType = getPageType(menuItemId)
        changeCurrentPageType(pageType)

        return true
    }

    // menuItemId에 따른 FragmentType 반환
    private fun getPageType(menuItemId: Int): FragmentType {
        return when(menuItemId) {
            R.id.detail -> FragmentType.FRAGMENT1
            R.id.grid -> FragmentType.FRAGMENT2
            R.id.user -> FragmentType.FRAGMENT4
            else -> throw IllegalArgumentException("Not found menu item")
        }
    }

    // 현재 FragmentType과 비교하여 같으면 return, 다르면 변경
    private fun changeCurrentPageType(fragmentType: FragmentType) {
        if(currentFragmentType.value == fragmentType) return

        viewModelScope.launch {
            _currentFragmentType.value = fragmentType
        }
    }
}