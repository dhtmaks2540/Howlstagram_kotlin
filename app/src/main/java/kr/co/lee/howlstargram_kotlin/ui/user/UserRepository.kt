package kr.co.lee.howlstargram_kotlin.ui.user

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid val currentUserId: String,
) {
    // 유저 정보 및 게시글 불러오기
    suspend fun getUserAndContentDTOs(uid: String): Pair<User, List<ContentDTO>> {
        return withContext(ioDispatcher) {
            val userSnapShot = fireStore.collection("users")
                .document(uid)
                .get().await()

            val profileSnapShot = fireStore.collection("profileImages")
                .document(uid)
                .get().await()

            val userDTOItem = userSnapShot.toObject(UserDTO::class.java)
            val user = User(
                userDTO = userDTOItem!!,
                profileSnapShot.data?.get("image").toString(),
                userUid = userSnapShot.id
            )

            val snapShot = fireStore.collection("images")
                .whereEqualTo("uid", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20)
                .get().await()

            val contentDTOs = ArrayList<ContentDTO>()

            snapShot.documents.forEach { documentSnapshot ->
                val contentDTO = documentSnapshot.toObject(ContentDTO::class.java)
                contentDTOs.add(contentDTO!!)
            }

            Pair(user, contentDTOs)
        }
    }

    // 팔로우 요청에 대한 내 팔로잉 정보 수정
    suspend fun saveMyAccount(userUid: String) {
        withContext(ioDispatcher) {
            val tsDocFollowing = fireStore.collection("users").document(currentUserId)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollowing).toObject(UserDTO::class.java)
                if (followDTOItem == null) {
                    followDTOItem = UserDTO()
                    followDTOItem.followingCount = 1
                    followDTOItem.followings[userUid] = true

                    transaction.set(tsDocFollowing, followDTOItem)
                    return@runTransaction
                }

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
            }
        }
    }

    // 팔로우 요청에 대한 상대방 팔로워 정보 수정
    suspend fun saveThirdPerson(userUid: String): Task<UserDTO> {
        return withContext(ioDispatcher) {
            val tsDocFollower = fireStore.collection("users").document(userUid)
            fireStore.runTransaction { transaction ->
                var followDTOItem = transaction.get(tsDocFollower).toObject(UserDTO::class.java)!!

                if (followDTOItem.followers.containsKey(currentUserId)) {
                    // cancel
                    followDTOItem.followerCount = followDTOItem.followerCount - 1
                    followDTOItem.followers.remove(currentUserId)
                } else {
                    // add
                    followDTOItem.followerCount = followDTOItem.followerCount + 1
                    followDTOItem.followers[currentUserId] = true
                }

                transaction.set(tsDocFollower, followDTOItem)
                followDTOItem
            }
        }
    }
}