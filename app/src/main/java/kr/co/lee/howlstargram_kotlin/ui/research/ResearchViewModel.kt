package kr.co.lee.howlstargram_kotlin.ui.research

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
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

@HiltViewModel
class ResearchViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid val currentUserUid: String,
): ViewModel() {
    private val _content = MutableLiveData<Content>()
    val content: LiveData<Content> = _content

    private val _isFollow = MutableLiveData<Boolean>()
    val isFollow: LiveData<Boolean> = _isFollow

    private val _isMyPost = MutableLiveData<Boolean>()
    val isMyPost: LiveData<Boolean> = _isMyPost

    fun getBundleData(contentDTO: ContentDTO?, contentUid: String?) {
        viewModelScope.launch {
            if(contentDTO != null && contentUid != null) {
                val result = requestLoadImage(contentDTO, contentUid)
                val isFollow = requestIsFollow(currentUserUid, contentDTO)
                _content.postValue(result)
                _isFollow.postValue(isFollow)
                _isMyPost.postValue(currentUserUid != contentDTO.uid)
            }
        }
    }

    fun favoriteEvent() {
        viewModelScope.launch {
            content.value?.let {
                requestFavoriteEvent(it.contentUid!!)
            }
        }
    }

    // 팔로우 요청
    fun requestFollow(): Job {
        val job = viewModelScope.launch {
            coroutineScope {
                saveMyAccount(content.value?.contentDTO?.uid!!, currentUserUid)
                saveThirdPerson(content.value?.contentDTO?.uid!!, currentUserUid)
            }
        }

        return job
    }

    // 팔로우 상태
    private suspend fun requestIsFollow(uid: String, contentDTO: ContentDTO): Boolean {
        return withContext(ioDispatcher) {
            val myUserSnapShot = fireStore.collection("users").document(uid).get().await()
            val isFollow = (myUserSnapShot.data?.get("followings") as Map<*, *>).containsKey(contentDTO.uid)

            isFollow
        }
    }

    // 게시글 불러오기
    private suspend fun requestLoadImage(contentDTO: ContentDTO, contentUid: String): Content {
        return withContext(ioDispatcher) {
            val profileShot = fireStore
                .collection("profileImages")
                .document(contentDTO.uid!!)
                .get()
                .await()

            val commentShot = fireStore
                .collection("images")
                .document(contentUid)
                .collection("comments").get().await()

            Content(contentDTO, contentUid, profileShot.data?.get("image").toString(), commentShot.size())
        }
    }

    // 좋아요 이벤트
    private suspend fun requestFavoriteEvent(contentUid: String) {
        withContext(ioDispatcher) {
            val tsDoc = fireStore.collection("images").document(contentUid)
            fireStore.runTransaction { transaction ->
                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)

                if(contentDTO?.favorites?.containsKey(currentUserUid)!!) {
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(currentUserUid)
                } else {
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[currentUserUid] = true
                }

                transaction.set(tsDoc, contentDTO)
                _content.postValue(content.value?.copy(contentDTO = contentDTO))
            }
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
                val isFollow = followDTOItem.followings.containsKey(userUid)
                _isFollow.postValue(isFollow)
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