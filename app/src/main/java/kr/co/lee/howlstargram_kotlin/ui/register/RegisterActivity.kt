package kr.co.lee.howlstargram_kotlin.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityRegisterBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>(R.layout.activity_register) {
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            viewModel = registerViewModel
        }

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.register_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_register -> {
                registerViewModel.signUp()
                if (registerViewModel.success.value == false) {
                    showToast("회원가입에 실패했습니다..")
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        setToolbar()
        observeLiveData()
    }

    // 툴바 설정
    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun observeLiveData() {
        registerViewModel.user.observe(this) {
            // 유저 정보 입력 후 MainActivity로 이동
            MainScope().launch {
                if (registerViewModel.userEmail.value.isNullOrEmpty() ||
                    registerViewModel.userPassword.value.isNullOrEmpty() ||
                    registerViewModel.userName.value.isNullOrEmpty() ||
                    registerViewModel.userNickName.value.isNullOrEmpty()
                ) {
                    showToast("항목을 모두 채워주세요")
                    return@launch
                }

                registerViewModel.insertUser(it)
                setResult(RESULT_OK)
                intent.putExtra("user", it)
                finish()
            }
        }
    }

}