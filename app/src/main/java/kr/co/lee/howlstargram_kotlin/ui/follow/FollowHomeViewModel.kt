package kr.co.lee.howlstargram_kotlin.ui.follow

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.utilites.*
import javax.inject.Inject

@HiltViewModel
class FollowHomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val tabType = savedStateHandle.get<TabType>(TAB_TYPE)
        ?: throw IllegalStateException("There is no tab.")
    val userNickName = savedStateHandle.get<String>(USER_NICKNAME)
        ?: throw IllegalStateException("There is no userNickName.")
    val followers = savedStateHandle.get<Map<String, Boolean>>(FOLLOWER)
        ?: throw IllegalStateException("There is no followers.")
    val followings = savedStateHandle.get<Map<String, Boolean>>(FOLLOWING)
        ?: throw IllegalStateException("There is no followings.")

    private val _follow = MutableLiveData(Pair(followers, followings))
    val follow: LiveData<Pair<Map<String, Boolean>, Map<String, Boolean>>> = _follow
}