package kr.co.lee.howlstargram_kotlin.ui.follow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import kr.co.lee.howlstargram_kotlin.utilites.TabType
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid val currentUserUid: String,
) : ViewModel() {
    private val _follow = MutableLiveData<Map<String, Boolean>>()
    val follow: LiveData<Map<String, Boolean>> = _follow

    private val _followDTOs = MutableLiveData<List<FavoriteDTO>>()
    val followDTOs: LiveData<List<FavoriteDTO>> = _followDTOs

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    fun getBundleData(follow: Map<String, Boolean>?) {
        viewModelScope.launch {
            follow?.let {
                _follow.postValue(it)
            }
        }
    }

    // 팔로우 또는 팔로잉 불러오기
    fun loadFollow(follow: Map<String, Boolean>) {
        viewModelScope.launch {
            val result = loadFollows(follow, currentUserUid)
            _followDTOs.postValue(result)
        }
    }

    // 팔로우 요청
    fun requestFollow(userUid: String, position: Int): Job {
        val job = viewModelScope.launch {
            coroutineScope {
                saveMyAccount(userUid,currentUserUid, position)
                saveThirdPerson(userUid, currentUserUid)
            }
        }

        return job
    }

    // Follower or Following 불러오는 메서드
    private suspend fun loadFollows(follow: Map<String, Boolean>, uid: String): ArrayList<FavoriteDTO> {
        return withContext(ioDispatcher) {
            val followDTOItems = ArrayList<FavoriteDTO>()

            for (key in follow.keys) {
                val myUserSnapShot = fireStore.collection("users").document(uid).get().await()
                val userSnapShot = fireStore.collection("users").document(key).get().await()
                val profileSnapShot = fireStore.collection("profileImages").document(key).get().await()
                val userItem = userSnapShot.toObject(UserDTO::class.java)
                val isFollow = (myUserSnapShot.data?.get("followings") as Map<*, *>).containsKey(key)

                userItem?.let {
                    val followDTO = FavoriteDTO(userUid = key,
                        profileUrl = profileSnapShot.data?.get("image").toString(),
                        userName = it.userName,
                        userNickName = it.userNickName,
                        isFollow = isFollow
                    )
                    followDTOItems.add(followDTO)
                }
            }

            followDTOItems
        }
    }

    // 팔로우 요청에 대한 내 팔로잉 정보 수정
    private suspend fun saveMyAccount(userUid: String, uid: String, position: Int) {
        withContext(ioDispatcher) {
            val tsDocFollowing = fireStore.collection("users").document(uid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollowing).toObject(UserDTO::class.java)
                if(followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followingCount = 1
                    followDTOItem.followings[userUid] = true

                    transaction.set(tsDocFollowing, followDTOItem)
                    return@runTransaction
                }

                if(followDTOItem.followings.containsKey(userUid)) {
                    // remove
                    followDTOItem.followingCount = followDTOItem.followingCount - 1
                    followDTOItem.followings.remove(userUid)
                } else {
                    // add
                    followDTOItem.followingCount = followDTOItem.followingCount + 1
                    followDTOItem.followings[userUid] = true
                }

                val isFollow = followDTOItem.followings.containsKey(userUid)

                transaction.set(tsDocFollowing, followDTOItem)
                val newFollowDTOs = followDTOs.value?.toMutableList()
                newFollowDTOs?.set(position, newFollowDTOs[position].copy(isFollow = isFollow))
                _followDTOs.postValue(newFollowDTOs!!)
            }
        }
    }

    // 팔로우 요청에 대한 상대방 팔로워 정보 수정
    private suspend fun saveThirdPerson(userUid: String, uid: String) {
        withContext(ioDispatcher) {
            val tsDocFollower = fireStore.collection("users").document(userUid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollower).toObject(UserDTO::class.java)
                if(followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followerCount = 1
                    followDTOItem.followers[uid] = true

                    transaction.set(tsDocFollower, followDTOItem)
                    return@runTransaction
                }

                if(followDTOItem.followers.containsKey(uid)) {
                    // cancel
                    followDTOItem.followerCount = followDTOItem.followerCount - 1
                    followDTOItem.followers.remove(uid)
                } else {
                    // add
                    followDTOItem.followerCount = followDTOItem.followerCount + 1
                    followDTOItem.followers[uid] = true
                }

                transaction.set(tsDocFollower, followDTOItem)
            }

        }
    }
}