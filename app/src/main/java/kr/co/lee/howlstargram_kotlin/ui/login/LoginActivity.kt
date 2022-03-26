package kr.co.lee.howlstargram_kotlin.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityLoginBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel>()
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    // Intent의 결과를 받기 위한 ActivityResultLauncher
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            activityResult.data?.let {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(it)
                if(result?.isSuccess == true) {
                    val account = result.signInAccount
                    // Second Step
                    firebaseAuthWithGoogle(account)
                }
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewModel = loginViewModel
            btEmailLogin.setOnClickListener { signInAndSignUp() }
            // Google Login First Step
            btGoogleSignIn.setOnClickListener { googleLogin() }
        }

        init()
    }

    // 초기화 
    private fun init() {
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("375438055963-8aervd706mmof7oublerf0rnnq097vlu.apps.googleusercontent.com") // Google Play Services default_web_client_id
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    // LiveData observe
    private fun subscribeLiveData() {

    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        println("FireBaseAuth!!")
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login
                    moveMainPage(task.result?.user)
                } else {
                    // Show the error Message
                    showToast(task.exception?.message.toString())
                }
            }
    }

    // 구글 로그인 메소드
    private fun googleLogin() {
        val signInClient = googleSignInClient.signInIntent
        launcher.launch(signInClient)
    }

    // 회원가입 메소드
    private fun signInAndSignUp() {
        auth.createUserWithEmailAndPassword(
            loginViewModel.email.value.toString(),
            loginViewModel.password.value.toString()
        ).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    // Create a user account
                    moveMainPage(task.result?.user)
                }
                task.exception?.message.isNullOrEmpty() -> {
                    // Show the error message
                    showToast(task.exception?.message.toString())
                }
                else -> {
                    // Login if you have account
                    signInEmail()
                }
            }
        }
    }

    // 로그인 메소드
    private fun signInEmail() {
        auth.signInWithEmailAndPassword(
            loginViewModel.email.toString(),
            loginViewModel.password.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Login
                moveMainPage(task.result?.user)
            } else {
                // Show the error Message
                showToast(task.exception?.message.toString())
            }
        }
    }
    
    // 다음 페이지로 이동하는 메소드
    private fun moveMainPage(user: FirebaseUser?) {
        // user가 null이 아니라면
        user?.let {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}