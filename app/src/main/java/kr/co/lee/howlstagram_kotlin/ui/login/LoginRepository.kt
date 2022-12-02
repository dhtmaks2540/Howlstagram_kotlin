package kr.co.lee.howlstagram_kotlin.ui.login

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kr.co.lee.howlstagram_kotlin.di.IoDispatcher
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val fireAuth: FirebaseAuth,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    // 로그인 요청
    suspend fun requestSignInEmail(email: String, password: String): Task<AuthResult> {
        return withContext(ioDispatcher) {
            fireAuth.signInWithEmailAndPassword(email, password)
        }
    }
}