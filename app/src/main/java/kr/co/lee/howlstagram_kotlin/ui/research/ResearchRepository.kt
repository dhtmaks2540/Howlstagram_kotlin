package kr.co.lee.howlstagram_kotlin.ui.research

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstagram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstagram_kotlin.di.IoDispatcher
import kr.co.lee.howlstagram_kotlin.model.Content
import kr.co.lee.howlstagram_kotlin.model.ContentDTO
import kr.co.lee.howlstagram_kotlin.model.ResearchContent
import kr.co.lee.howlstagram_kotlin.model.UserDTO
import javax.inject.Inject

class ResearchRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid val currentUserUid: String,
) {
    // 유저 정보와 게시글 호출
    suspend fun getUserAndContent(
        contentDTO: ContentDTO,
        contentUid: String
    ): ResearchContent {
        return withContext(ioDispatcher) {
            val myUserSnapShot =
                fireStore.collection("users").document(currentUserUid).get().await()
            val isFollow =
                (myUserSnapShot.data?.get("followings") as Map<*, *>).containsKey(contentDTO.uid)

            val profileShot = fireStore
                .collection("profileImages")
                .document(contentDTO.uid!!)
                .get()
                .await()

            val commentShot = fireStore
                .collection("images")
                .document(contentUid)
                .collection("comments").get().await()

            val content = Content(
                contentDTO,
                contentUid,
                profileShot.data?.get("image").toString(),
                commentShot.size()
            )
            val isMyPost = currentUserUid != contentDTO.uid

            ResearchContent(content, isFollow, isMyPost)
        }
    }

    // 팔로우 요청에 대한 내 팔로잉 정보 수정
    suspend fun saveMyAccount(userUid: String): Task<Boolean> {
        val job = withContext(ioDispatcher) {
            val tsDocFollowing = fireStore.collection("users").document(currentUserUid)
            fireStore.runTransaction { transaction ->
                val followDTOItem = transaction.get(tsDocFollowing).toObject(UserDTO::class.java)!!
                if (followDTOItem.followings.containsKey(userUid)) {
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
                isFollow
            }
        }

        return job
    }

    // 팔로우 요청에 대한 상대방 팔로워 정보 수정
    suspend fun saveThirdPerson(userUid: String) {
        withContext(ioDispatcher) {
            val tsDocFollower = fireStore.collection("users").document(userUid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollower).toObject(UserDTO::class.java)
                if (followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followerCount = 1
                    followDTOItem.followers[currentUserUid] = true

                    transaction.set(tsDocFollower, followDTOItem)
                    return@runTransaction
                }

                if (followDTOItem.followers.containsKey(currentUserUid)) {
                    // cancel
                    followDTOItem.followerCount = followDTOItem.followerCount - 1
                    followDTOItem.followers.remove(currentUserUid)
                } else {
                    // add
                    followDTOItem.followerCount = followDTOItem.followerCount + 1
                    followDTOItem.followers[currentUserUid] = true
                }

                transaction.set(tsDocFollower, followDTOItem)
            }
        }
    }

    // 좋아요 이벤트
    suspend fun requestFavoriteEvent(contentUid: String): Task<ContentDTO> {
        val job = withContext(ioDispatcher) {
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
                contentDTO
            }
        }

        return job
    }
}