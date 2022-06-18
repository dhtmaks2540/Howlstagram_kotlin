package kr.co.lee.howlstargram_kotlin.ui.user

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
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid val currentUserId: String,
) : ViewModel() {
    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    private val _userDTO = MutableLiveData<UserDTO>()
    val userDTO: LiveData<UserDTO> = _userDTO

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _contentDTOs = MutableLiveData<List<ContentDTO>>()
    val contentDTOs: LiveData<List<ContentDTO>> = _contentDTOs

    // 프로필 사진 업데이트
    fun updateProfileUrl(profileUrl: String?) {
        viewModelScope.launch {
            profileUrl?.let {
                _user.postValue(user.value?.copy(profileUrl = it))
            }
        }
    }

    // args Data 획득
    fun setIntentData(uid: String?) {
        uid?.let {
            viewModelScope.launch {
                _uid.postValue(it)
            }
        }
    }

    // 내 Uid 획득
    fun loadMyUid() {
        viewModelScope.launch {
            _uid.postValue(currentUserId)
        }
    }

    // 팔로우 요청
    fun requestFollow(): Job {
        val job = viewModelScope.launch {
            coroutineScope {
                saveMyAccount(uid.value!!, currentUserId)
                saveThirdPerson(uid.value!!, currentUserId)
            }
        }

        return job
    }

    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
        }
    }

    fun loadUser(): Job {
        val job = viewModelScope.launch {
            coroutineScope {
                val userResult = requestUser(uid.value!!)
                val contentResult = requestContentDTOs(uid.value!!)

                _user.postValue(userResult)
                _contentDTOs.postValue(contentResult)
            }
        }

        return job
    }

    // 유저 정보 요청
    private suspend fun requestUser(uid: String): User {
        return withContext(ioDispatcher) {
            val userSnapShot = fireStore.collection("users")
                .document(uid)
                .get().await()

            val profileSnapShot = fireStore.collection("profileImages")
                .document(uid)
                .get().await()

            val userDTOItem = userSnapShot.toObject(UserDTO::class.java)
            val user = User(userDTO = userDTOItem!!, profileSnapShot.data?.get("image").toString(), userUid = userSnapShot.id)
            user
        }
    }

    // 사용자가 작성한 글 불러오기
    private suspend fun requestContentDTOs(uid: String): ArrayList<ContentDTO> {
        return withContext(ioDispatcher) {
            val snapShot = fireStore.collection("images")
                .whereEqualTo("uid", uid)
                .get().await()

            val contentDTOs = ArrayList<ContentDTO>()

            snapShot.documents.forEach { documentSnapshot ->
                val contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
                contentDTOs.add(contentDTO!!)
            }
            contentDTOs
        }
    }

    // 팔로우 요청에 대한 내 팔로잉 정보 수정
    private suspend fun saveMyAccount(userUid: String, uid: String) {
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

                transaction.set(tsDocFollowing, followDTOItem)
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
                followDTOItem.let {
                    _user.postValue(user.value?.copy(userDTO = followDTOItem))
                }
            }
        }
    }
}