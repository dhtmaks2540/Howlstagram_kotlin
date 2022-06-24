package kr.co.lee.howlstargram_kotlin.ui.like

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import javax.inject.Inject

class LikeRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid val currentUserUid: String,
) {
    // 좋아요 호출하기
    fun getAllFavorites(favorites: Map<String, Boolean>) = flow<UiState<List<FavoriteDTO>>> {
        val favoriteDTOItems = ArrayList<FavoriteDTO>()
        favorites.forEach { (userId, _) ->
            val userSnapShot = fireStore.collection("users")
                .document(userId)
                .get().await()

            val profileSnapShot = fireStore.collection("profileImages")
                .document(userId)
                .get().await()

            val isFollow =
                (userSnapShot.data?.get("followers") as Map<*, *>).containsKey(currentUserUid)

            val userItem = userSnapShot.toObject(UserDTO::class.java)

            userItem?.let {
                val favoriteDTO = FavoriteDTO(
                    userUid = userId,
                    profileUrl = profileSnapShot.data?.get("image").toString(),
                    userName = it.userName,
                    userNickName = it.userNickName,
                    isFollow = isFollow
                )

                favoriteDTOItems.add(favoriteDTO)
            }
        }

        emit(UiState.Success(favoriteDTOItems))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)

    // 팔로우 요청에 대한 내 정보 저장
    suspend fun saveMyAccount(userUid: String) = flow<UiState<Boolean>> {
        val tsDocFollowing = fireStore.collection("users").document(currentUserUid)
        var followDTO = tsDocFollowing.get().await().toObject(UserDTO::class.java)

        if (followDTO == null) {
            followDTO = UserDTO()
            followDTO.followingCount = 1
            followDTO.followings[userUid] = true

            fireStore.collection("users").document(currentUserUid).set(followDTO).await()

            return@flow
        }

        if (followDTO.followings.containsKey(userUid)) {
            // remove
            followDTO.followingCount = followDTO.followingCount - 1
            followDTO.followings.remove(userUid)
        } else {
            // add
            followDTO.followingCount = followDTO.followingCount + 1
            followDTO.followings[userUid] = true
        }

        fireStore.collection("users").document(currentUserUid).set(followDTO).await()
        val isFollow = followDTO.followings.containsKey(userUid)
        emit(UiState.Success(isFollow))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)

    // 팔로우 요청에 대한 상대방 정보 저장
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
}