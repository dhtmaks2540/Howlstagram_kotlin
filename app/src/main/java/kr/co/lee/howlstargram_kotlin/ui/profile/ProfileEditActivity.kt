package kr.co.lee.howlstargram_kotlin.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityProfileBinding
import kr.co.lee.howlstargram_kotlin.ui.gallery.GalleryActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity<ActivityProfileBinding>(R.layout.activity_profile) {
    private val profileViewModel: ProfileViewModel by viewModels()
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
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.action_finish -> {
                lifecycleScope.launch {
                    // userName이나 userNickName이 수정되었다면 변경 요청 후 종료
                    if(profileViewModel.userName.value != binding.etUserName.text.toString().trim() || profileViewModel.userNickName.value != binding.etUserNickname.text.toString().trim()) {
                        val job = profileViewModel.updateUser(
                            binding.etUserName.text.toString().trim(),
                            binding.etUserNickname.text.toString().trim()
                        )

                        job.join()
                    }

                    setResult(RESULT_OK)
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        binding.apply {
            viewModel = profileViewModel

            // 프로필 이미지 수정
            tvEditProfile.setOnClickListener {
                val intent = Intent(this@ProfileEditActivity, GalleryActivity::class.java)
                intent.putExtra(IMAGE_TYPE, ImageType.PROFILE_TYPE)
                profileLauncher.launch(intent)
            }
        }

        // Launcher 초기화
        profileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                val profileUrl = it.data?.getStringExtra(PROFILE_URL)
                profileViewModel.updateProfile(profileUrl)
            }
        }

        getIntentData()
        setToolbar()
    }

    // 툴바 설정
    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_close_black_20)
    }

    // Intent Data 획득
    private fun getIntentData() {
        val profileUrl = intent.getStringExtra(PROFILE_URL)
        val userName = intent.getStringExtra(USER_NAME)
        val userNickName = intent.getStringExtra(USER_NICKNAME)
        profileViewModel.getIntentData(profileUrl = profileUrl, userName = userName, userNickName = userNickName)
    }
}