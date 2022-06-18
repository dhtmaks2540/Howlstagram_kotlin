package kr.co.lee.howlstargram_kotlin.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityLoginBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.ui.register.RegisterActivity
import kr.co.lee.howlstargram_kotlin.utilites.USER
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val loginViewModel: LoginViewModel by viewModels()

    // FirebaseAuth Instance(Firebase 인증 객체)
    @Inject
    lateinit var auth: FirebaseAuth

    // Intent의 결과를 받기 위한 ActivityResultLauncher
    private val registerLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val user = intent.getParcelableExtra<FirebaseUser>(USER)
            moveMainPage(user)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            viewModel = loginViewModel
            btEmailLogin.setOnClickListener { signInEmail() }
            btEmailSignup.setOnClickListener { signUp() }
        }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser)
    }

    // 회원가입
    private fun signUp() {
        val intent = Intent(this, RegisterActivity::class.java)
        registerLauncher.launch(intent)
    }

    // 로그인 메소드
    private fun signInEmail() {
        // 유효성 검사 후 로그인
        auth.signInWithEmailAndPassword(
            loginViewModel.email.value.toString().trim(),
            loginViewModel.password.value.toString().trim()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Login
                moveMainPage(task.result?.user)
            } else {
                // Show the error Message
                showToast(resources.getString(R.string.checkout_id_password))
            }
        }
    }

    // 다음 페이지로 이동하는 메소드
    private fun moveMainPage(user: FirebaseUser?) {
        // user가 null이 아니라면
        user?.let {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
