package kr.co.lee.howlstargram_kotlin.ui.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.utilites.TabType
import javax.inject.Inject

@HiltViewModel
class FollowHomeViewModel @Inject constructor() : ViewModel() {
    private val _follow = MutableLiveData<Pair<Map<String, Boolean>, Map<String, Boolean>>>()
    val follow: LiveData<Pair<Map<String, Boolean>, Map<String, Boolean>>> = _follow

    private val _userNickName = MutableLiveData<String>()
    val userNickName: LiveData<String> = _userNickName

    private val _tabType = MutableLiveData<TabType>()
    val tabType: LiveData<TabType> = _tabType

    // Bundle Data
    fun getBundleData(followers: Map<String, Boolean>?, followings: Map<String, Boolean>?, tabType: TabType?, userNickName: String?) {
        viewModelScope.launch {
            tabType?.let {
                _tabType.postValue(it)
            }

            userNickName?.let {
                _userNickName.postValue(it)
            }

            if(followers != null && followings != null) {
                val pair = Pair(followers, followings)
                _follow.postValue(pair)
            }
        }
    }
}