package kr.co.lee.howlstargram_kotlin.ui.profile

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kr.co.lee.howlstargram_kotlin.di.CurrentUserUid
import kr.co.lee.howlstargram_kotlin.di.IoDispatcher
import kr.co.lee.howlstargram_kotlin.model.UserDTO
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @CurrentUserUid private val currentUserUid: String,
) {
    // 사용자 정보 업데이트
    suspend fun requestUpdateUser(userName: String, userNickName: String): Task<Transaction> {
        val tsDoc = fireStore.collection("users").document(currentUserUid)
        return withContext(ioDispatcher) {
            fireStore.runTransaction { transaction ->
                val userDTO = transaction.get(tsDoc).toObject(UserDTO::class.java)
                userDTO?.userName = userName
                userDTO?.userNickName = userNickName
                transaction.set(tsDoc, userDTO!!)
            }
        }
    }
}