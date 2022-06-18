package kr.co.lee.howlstargram_kotlin.ui.addphoto

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityAddPhotoBinding
import kr.co.lee.howlstargram_kotlin.utilites.URI
import javax.inject.Inject

@AndroidEntryPoint
class AddPhotoActivity : BaseActivity<ActivityAddPhotoBinding>(R.layout.activity_add_photo) {
    private val addViewModel by viewModels<AddPhotoViewModel>()

    @Inject
    lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewModel = addViewModel
        }

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_photo_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_finish -> {
                lifecycleScope.launch {
                    try {
                        val job = addViewModel.contentUploadImage()
                        job.join()
                    } catch (e: Exception) {
                        showToast(message = "AddPhotoActivity :  ${e.message}")
                    } finally {
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }

        return true
    }

    // 초기화 메서드
    private fun init() {
        getIntentData()
        setToolbar()
        observeLiveDate()
    }

    // Intent Data 획득(Image Uri)
    private fun getIntentData() {
        addViewModel.setUri(intent.getStringExtra(URI)!!)
    }

    // 툴바 설정
    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun observeLiveDate() {
        // 선택된 이미지
        addViewModel.uri.observe(this) {
            Glide.with(binding.ivAddphoto.context)
                .load(it)
                .centerCrop()
                .into(binding.ivAddphoto)
        }
    }
}
