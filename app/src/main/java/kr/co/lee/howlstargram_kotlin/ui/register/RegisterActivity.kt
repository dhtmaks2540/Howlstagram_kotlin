package kr.co.lee.howlstargram_kotlin.ui.register

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityRegisterBinding
import kr.co.lee.howlstargram_kotlin.utilites.USER
import kr.co.lee.howlstargram_kotlin.utilites.forEachChildView

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {
    override val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            vm = viewModel
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
            }

            R.id.action_register -> {
                if (checkUserInfo()) {
                    binding.layoutRoot.forEachChildView { it.isEnabled = false }
                    binding.loadingBar.visibility = View.VISIBLE
                    viewModel.signUp()
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
        return if(viewModel.userEmail.value.isNullOrEmpty() || viewModel.userPassword.value.isNullOrEmpty() ||
            viewModel.userName.value.isNullOrEmpty() || viewModel.userNickName.value.isNullOrEmpty()
        ) {
            showToast("모든 항목을 채워주세요.")
            false
        } else if(viewModel.userEmail.value!!.trim().length < 5 || viewModel.userPassword.value!!.trim().length < 5) {
            showToast("아이디 또는 비밀번호는 최소 5글자 이상이어야 가능합니다.")
            false
        }
        else if(viewModel.userEmail.value!!.trim().length > 25 || viewModel.userPassword.value!!.trim().length > 25 ||
                viewModel.userNickName.value!!.trim().length > 15 || viewModel.userName.value!!.trim().length > 15) {
                    if(viewModel.userEmail.value!!.trim().length > 25 || viewModel.userPassword.value!!.trim().length > 25) {
                        showToast("아이디 또는 비밀번호는 최대 25자까지 가능합니다.")
                    } else if(viewModel.userNickName.value!!.trim().length > 15 || viewModel.userName.value!!.trim().length > 15) {
                        showToast("이름 또는 닉네임은 최대 15자까지 가능합니다.")
                    }
            false
        } else {
            true
        }
    }

    private fun observeLiveData() {
        viewModel.user.observe(this) {
            // 유저 정보 입력 후 MainActivity로 이동
            lifecycleScope.launch {
                val task = viewModel.insertUser(it)

                task.addOnSuccessListener {
                    setResult(RESULT_OK)
                    intent.putExtra(USER, it)
                    finish()
                }.addOnFailureListener {
                    binding.layoutRoot.forEachChildView { it.isEnabled = true }
                    binding.loadingBar.visibility = View.GONE
                    showToast("데이터베이스에 오류가 생겼습니다... 잠시후 다시 요청해주세요.")
                }
            }
        }

        viewModel.success.observe(this) { result ->
            if(!result) {
                binding.layoutRoot.forEachChildView { it.isEnabled = true }
                binding.loadingBar.visibility = View.GONE
                showToast("회원가입에 실패하였습니다...")
            }
        }
    }

}