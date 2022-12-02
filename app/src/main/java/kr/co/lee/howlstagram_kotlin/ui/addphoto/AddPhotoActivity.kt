package kr.co.lee.howlstagram_kotlin.ui.addphoto

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstagram_kotlin.R
import kr.co.lee.howlstagram_kotlin.base.BaseActivity
import kr.co.lee.howlstagram_kotlin.databinding.ActivityAddPhotoBinding
import kr.co.lee.howlstagram_kotlin.utilites.forEachChildView

@AndroidEntryPoint
class AddPhotoActivity : BaseActivity<ActivityAddPhotoBinding>(R.layout.activity_add_photo) {
    private val viewModel by viewModels<AddPhotoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            vm = viewModel
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
                    binding.loadingBar.visibility = View.VISIBLE
                    binding.layout.forEachChildView { it.isEnabled = false }
                    val task = viewModel.contentUploadImage()
                    task.addOnSuccessListener {
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
        setToolbar()
    }

    // 툴바 설정
    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
