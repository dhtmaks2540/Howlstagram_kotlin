package kr.co.lee.howlstargram_kotlin.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityLoginBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.ui.register.RegisterActivity
import kr.co.lee.howlstargram_kotlin.utilites.USER
import kr.co.lee.howlstargram_kotlin.utilites.forEachChildView
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels()

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
            vm = viewModel
            handler = this@LoginActivity
        }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser)
    }

    // 회원가입
    fun signUp() {
        val intent = Intent(this, RegisterActivity::class.java)
        registerLauncher.launch(intent)
    }

    // 로그인
    fun signInEmail() {
        lifecycleScope.launch {
            binding.loadingBar.visibility = View.VISIBLE
            binding.layoutRoot.forEachChildView { it.isEnabled = false }
            val task = viewModel.signInEmail()
            task.addOnCompleteListener {
                if(task.isSuccessful) {
                    moveMainPage(task.result?.user)
                } else {
                    // Show the error Message
                    showToast(resources.getString(R.string.checkout_id_password))
                    binding.loadingBar.visibility = View.GONE
                    binding.layoutRoot.forEachChildView { it.isEnabled = true }
                }
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
