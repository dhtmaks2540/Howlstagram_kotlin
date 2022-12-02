package kr.co.lee.howlstagram_kotlin.ui.search

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kr.co.lee.howlstagram_kotlin.di.IoDispatcher
import kr.co.lee.howlstagram_kotlin.model.User
import kr.co.lee.howlstagram_kotlin.model.UserDTO
import kr.co.lee.howlstagram_kotlin.utilites.UiState
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    // 유저 정보 불러오기
    fun getAllUsers(text: String) = flow<UiState<List<User>>> {
        emit(UiState.Loading)
        val userShot = fireStore.collection("users")
            .limit(20)
            .get().await()

        val userItems = ArrayList<User>()

        userShot.forEach { documentSnapshot ->
            val nameData = documentSnapshot.getString("userName")
            val nickNameData = documentSnapshot.getString("userNickName")

            if((nickNameData != null && nickNameData.contains(text)) || (nameData != null && nameData.contains(text))) {
                val item = documentSnapshot.toObject(UserDTO::class.java)
                val profileShot = fireStore.collection("profileImages").document(documentSnapshot.id).get().await()

                userItems.add(User(userDTO = item, profileUrl = profileShot.data?.get("image").toString(), userUid = documentSnapshot.id))
            }
        }

        emit(UiState.Success(userItems))
    }.catch {
        emit(UiState.Failed(it.message.toString()))
    }.flowOn(ioDispatcher)
}