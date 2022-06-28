package kr.co.lee.howlstargram_kotlin.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityEditProfileBinding
import kr.co.lee.howlstargram_kotlin.ui.gallery.GalleryActivity
import kr.co.lee.howlstargram_kotlin.utilites.IMAGE_TYPE
import kr.co.lee.howlstargram_kotlin.utilites.ImageType
import kr.co.lee.howlstargram_kotlin.utilites.PROFILE_URL
import kr.co.lee.howlstargram_kotlin.utilites.forEachChildView

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity<ActivityEditProfileBinding, ProfileViewModel>(R.layout.activity_edit_profile) {
    override val viewModel: ProfileViewModel by viewModels()
    private lateinit var profileLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.action_finish -> {
                lifecycleScope.launch {
                    // userName이나 userNickName이 수정되었다면 변경 요청 후 종료
                    if (
                        (viewModel.userName != binding.etUserName.text.toString().trim() ||
                        viewModel.userNickName != binding.etUserNickname.text.toString().trim()) && 
                        checkUserInfo()
                    ) {
                        binding.layoutRoot.forEachChildView { it.isEnabled = false }
                        binding.loadingBar.visibility = View.VISIBLE
                        val task = viewModel.updateUser(
                            binding.etUserName.text.toString().trim(),
                            binding.etUserNickname.text.toString().trim()
                        )
                        task.addOnSuccessListener {
                            setResult(RESULT_OK)
                            finish()
                        }
                    } else {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // 초기화 메서드
    private fun init() {
        binding.apply {
            vm = viewModel
            handler = this@ProfileEditActivity
        }

        // 프로필 Launcher 초기화
        profileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val profileUrl = it.data?.getStringExtra(PROFILE_URL)
                    viewModel.updateProfile(profileUrl)
                    setResult(RESULT_OK)
                    finish()
                }
            }

        setToolbar()
    }

    // 프로필 수정 이벤트
    fun profileClickListener() {
        val intent = Intent(this@ProfileEditActivity, GalleryActivity::class.java)
        intent.putExtra(IMAGE_TYPE, ImageType.PROFILE_TYPE)
        profileLauncher.launch(intent)
    }

    // 툴바 설정
    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_close_black_20)
    }

    // 이름, 닉네임 체크
    private fun checkUserInfo(): Boolean {
        return if (binding.etUserName.text.toString().trim().isEmpty() ||
            binding.etUserName.text.toString().trim().isBlank() ||
            binding.etUserNickname.text.toString().trim().isEmpty() ||
            binding.etUserNickname.text.toString().trim().isBlank()
        ) {
            showToast("모든 항목을 채워주세요.")
            false
        } else if (binding.etUserName.text.toString().trim().length > 15 ||
            binding.etUserName.text.toString().trim().length > 15
        ) {
            showToast("이름 또는 닉네임은 최대 15자까지 가능합니다.")
            false
        } else {
            true
        }
    }
}