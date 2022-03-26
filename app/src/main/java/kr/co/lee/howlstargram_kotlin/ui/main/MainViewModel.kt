package kr.co.lee.howlstargram_kotlin.ui.main

import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.utilites.PageType

class MainViewModel: ViewModel() {
    private val _currentPageType = MutableLiveData<PageType>(PageType.PAGE1)

    val currentPageType: LiveData<PageType>
        get() = _currentPageType

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
            R.id.action_home -> PageType.PAGE1
            R.id.action_search -> PageType.PAGE2
            R.id.action_add_photo -> PageType.PAGE3
            R.id.action_favorite_alarm -> PageType.PAGE4
            R.id.action_account -> PageType.PAGE5
            else -> throw IllegalArgumentException("Not found menu item")
        }
    }

    // 현재 PageType과 비교하여 같으면 return, 다르면 변경
    private fun changeCurrentPageType(pageType: PageType) {
        if(currentPageType.value == pageType) return

        _currentPageType.value = pageType
    }
}