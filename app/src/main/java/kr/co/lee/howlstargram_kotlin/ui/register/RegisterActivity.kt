package kr.co.lee.howlstargram_kotlin.ui.register

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityRegisterBinding
import kr.co.lee.howlstargram_kotlin.utilites.USER

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
            android.R.id.home -> {
                this.finish()
                println("HOME~~")
            }
            R.id.action_register -> {
                if (checkUserInfo()) {
                    registerViewModel.signUp()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // 초기화 메서드
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

    private fun checkUserInfo(): Boolean {
        return if(registerViewModel.userEmail.value.isNullOrEmpty() || registerViewModel.userPassword.value.isNullOrEmpty() ||
            registerViewModel.userName.value.isNullOrEmpty() || registerViewModel.userNickName.value.isNullOrEmpty()
        ) {
            showToast("모든 항목을 채워주세요.")
            false
        } else if(registerViewModel.userEmail.value!!.length < 5 || registerViewModel.userPassword.value!!.length < 5) {
            showToast("아이디 또는 비밀번호는 최소 5글자 이상이어야 가능합니다.")
            false
        }
        else if(registerViewModel.userEmail.value!!.length > 25 || registerViewModel.userPassword.value!!.length > 25 ||
                registerViewModel.userNickName.value!!.length > 15 || registerViewModel.userName.value!!.length > 15) {
                    if(registerViewModel.userEmail.value!!.length > 25 || registerViewModel.userPassword.value!!.length > 25) {
                        showToast("아이디 또는 비밀번호는 최대 25자까지 가능합니다.")
                    } else if(registerViewModel.userNickName.value!!.length > 15 || registerViewModel.userName.value!!.length > 15) {
                        showToast("이름 또는 닉네임은 최대 15자까지 가능합니다.")
                    }
            false
        } else {
            true
        }
    }

    private fun observeLiveData() {
        registerViewModel.user.observe(this) {
            // 유저 정보 입력 후 MainActivity로 이동
            lifecycleScope.launch {
                registerViewModel.insertUser(it)
                setResult(RESULT_OK)
                intent.putExtra(USER, it)
                finish()
            }
        }

        registerViewModel.success.observe(this) { result ->
            if(!result) {
                showToast("회원가입에 실패하였습니다...")
            }
        }
    }

}