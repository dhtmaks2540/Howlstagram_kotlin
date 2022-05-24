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
import kr.co.lee.howlstargram_kotlin.utilites.PageType
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth): ViewModel() {
    private val _currentPageType = MutableLiveData<PageType>(PageType.PAGE1)
    val currentPageType: LiveData<PageType> = _currentPageType

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    init {
        viewModelScope.launch {
            _userId.postValue(firebaseAuth.currentUser?.email)
            _uid.postValue(firebaseAuth.currentUser?.uid)
        }
    }

    // PageType에 따라 currentPageType 변경
    // BindingAdapter에 의해서 호출 될 메소드
    fun setCurrentFragment(item: MenuItem): Boolean {
        val menuItemId = item.itemId
        val pageType = getPageType(menuItemId)
        changeCurrentPageType(pageType)

        return true
    }

    // menuItemId에 따른 PageType 반환
    private fun getPageType(menuItemId: Int): PageType {
        return when(menuItemId) {
            R.id.detail -> PageType.PAGE1
            R.id.grid -> PageType.PAGE2
            R.id.reels -> PageType.PAGE3
            R.id.alarm -> PageType.PAGE4
            R.id.user -> PageType.PAGE5
            else -> throw IllegalArgumentException("Not found menu item")
        }
    }

    // 현재 PageType과 비교하여 같으면 return, 다르면 변경
    private fun changeCurrentPageType(pageType: PageType) {
        if(currentPageType.value == pageType) return

        _currentPageType.value = pageType
    }
}