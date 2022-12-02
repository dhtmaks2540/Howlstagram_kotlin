package kr.co.lee.howlstagram_kotlin.ui.register

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kr.co.lee.howlstagram_kotlin.di.IoDispatcher
import kr.co.lee.howlstagram_kotlin.model.UserDTO
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    // 회원가입 요청
    suspend fun requestSignUp(userEmail: String, userPassword: String): Task<AuthResult> {
        return withContext(ioDispatcher) {
           auth.createUserWithEmailAndPassword(userEmail, userPassword)
        }
    }

    // 유저 정보 데이터베이스에 입력
    suspend fun insertUserInformation(userItem: FirebaseUser, userName: String, userNickName: String): Task<FirebaseUser> {
        return withContext(ioDispatcher) {
            val tsDocUser = fireStore.collection("users").document(userItem.uid)
            fireStore.runTransaction { transaction ->
                val item = transaction.get(tsDocUser).toObject(UserDTO::class.java)
                if (item == null) {
                    val userDTO = UserDTO(
                        userEmail = userItem.email.toString(),
                        userName = userName,
                        userNickName = userNickName
                    )
                    transaction.set(tsDocUser, userDTO)
                }
                userItem
            }
        }
    }
}