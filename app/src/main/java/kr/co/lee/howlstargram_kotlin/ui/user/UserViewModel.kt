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
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth, @IoDispatcher private val ioDispatcher: CoroutineDispatcher): ViewModel() {
    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _uid = MutableLiveData<String>()
    val uid: LiveData<String> = _uid

    private val _currentUserId = MutableLiveData<String>()
    val currentUserId: LiveData<String> = _currentUserId

    private val _myProfile = MutableLiveData<Boolean>(false)
    val myProfile: LiveData<Boolean> = _myProfile

    private val _followDTO = MutableLiveData<UserDTO>()
    val userDTO: LiveData<UserDTO> = _followDTO

    private val _profileUrl = MutableLiveData<String?>()
    val profileUrl: LiveData<String?> = _profileUrl

    private val _contentDTOs = MutableLiveData<List<ContentDTO>>()
    val contentDTOs: LiveData<List<ContentDTO>> = _contentDTOs

    init {
        viewModelScope.launch {
            _currentUserId.postValue(firebaseAuth.currentUser?.uid)
        }
    }

    fun setProfileUrl(profileUrl: String) {
        viewModelScope.launch {
            _profileUrl.postValue(profileUrl)
        }
    }

    // args Data 획득
    fun setIntentData(userId: String, uid: String, profileUrl: String) {
        viewModelScope.launch {
            _userId.postValue(userId)
            _uid.postValue(uid)
            _profileUrl.postValue(profileUrl)
        }
    }

    // 내 프로필 사진인지
    fun setIsMyProfile(check: Boolean) {
        viewModelScope.launch {
            if(check) _myProfile.postValue(true)
            else _myProfile.postValue(false)
        }
    }

    // 팔로워, 팔로윙 정보 획득
    fun getFollowerAndFollowing(uid: String) {
        viewModelScope.launch(ioDispatcher) {
            val snapShot = fireStore.collection("users")
                .document(uid)
                .get()
                .await()

            val followDTOItem = snapShot.toObject(UserDTO::class.java)
            followDTOItem?.let {
                _followDTO.postValue(it)
            }
        }
    }

    // 내 Uid 획득
    fun loadMyUid() {
        viewModelScope.launch {
            _userId.postValue(firebaseAuth.currentUser?.email)
            _uid.postValue(firebaseAuth.currentUser?.uid)
        }
    }

    // ProfileUrl 획득
    fun loadMyProfileUrl() {
        viewModelScope.launch(ioDispatcher) {
            val profileShot = fireStore.collection("profileImages").document(uid.value!!).get().await()
            _profileUrl.postValue(profileShot.data?.get("image").toString())
        }
    }

    // 팔로우 요청
    fun requestFollow() {
        viewModelScope.launch {
            coroutineScope {
                saveMyAccount()
                saveThirdPerson()
            }
        }
    }

    fun loadContentDTO() {
        viewModelScope.launch {
            val result = loadContentDTOs()
            _contentDTOs.postValue(result)
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
        }
    }

    // 사용자가 작성한 글 불러오기
    private suspend fun loadContentDTOs(): ArrayList<ContentDTO> {
        return withContext(ioDispatcher) {
            val snapShot = fireStore.collection("images").whereEqualTo("uid", uid.value).get().await()
            val contentDTOs = ArrayList<ContentDTO>()
            snapShot.documents.forEach { documentSnapshot ->
                val contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
                contentDTOs.add(contentDTO!!)
            }
            contentDTOs
        }
    }

    // 팔로우 요청에 대한 내 팔로잉 정보 수정
    private suspend fun saveMyAccount() {
        withContext(ioDispatcher) {
            val tsDocFollowing = fireStore.collection("users").document(currentUserId.value!!)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollowing).toObject(UserDTO::class.java)
                if(followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followingCount = 1
                    followDTOItem.followings[uid.value!!] = true

                    transaction.set(tsDocFollowing, followDTOItem)
                    return@runTransaction
                }

                if(followDTOItem.followings.containsKey(uid.value!!)) {
                    // remove
                    followDTOItem.followingCount = followDTOItem.followingCount - 1
                    followDTOItem.followings.remove(uid.value!!)
                } else {
                    // add
                    followDTOItem.followingCount = followDTOItem.followingCount + 1
                    followDTOItem.followings[uid.value!!] = true
                }

                transaction.set(tsDocFollowing, followDTOItem)
            }
        }
    }

    // 팔로우 요청에 대한 상대방 팔로워 정보 수정
    private suspend fun saveThirdPerson() {
        withContext(ioDispatcher) {
            val tsDocFollower = fireStore.collection("users").document(uid.value!!)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollower).toObject(UserDTO::class.java)
                if(followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followerCount = 1
                    followDTOItem.followers[currentUserId.value!!] = true

                    transaction.set(tsDocFollower, followDTOItem)
                }

                if(followDTOItem.followers.containsKey(currentUserId.value!!)) {
                    // cancel
                    followDTOItem.followerCount = followDTOItem.followerCount - 1
                    followDTOItem.followers.remove(currentUserId.value!!)
                } else {
                    // add
                    followDTOItem.followerCount = followDTOItem.followerCount + 1
                    followDTOItem.followers[currentUserId.value!!] = true
                }

                transaction.set(tsDocFollower, followDTOItem)
                _followDTO.postValue(followDTOItem!!)
            }
        }
    }
}