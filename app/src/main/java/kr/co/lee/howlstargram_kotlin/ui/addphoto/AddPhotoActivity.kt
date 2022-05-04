package kr.co.lee.howlstargram_kotlin.ui.addphoto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityAddPhotoBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddPhotoActivity: BaseActivity<ActivityAddPhotoBinding>(R.layout.activity_add_photo) {
    private val addViewModel by viewModels<AddPhotoViewModel>()

    @Inject
    lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewModel = addViewModel
        }

        init()
        observeLiveDate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_photo_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_finish -> {
                addViewModel.contentUploadImage()
            }
        }

        return true
    }

    private fun init() {
        addViewModel.setUri(intent.getStringExtra("uri")!!)
        toolbarInit()
    }

    private fun toolbarInit() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun observeLiveDate() {
        addViewModel.contentDTO.observe(this) {
            fireStore.collection("images").document().set(addViewModel.contentDTO.value!!)
            setResult(Activity.RESULT_OK)

            finish()
        }

        // 선택된 이미지
        addViewModel.uri.observe(this) {
            Glide.with(binding.ivAddphoto.context)
                .load(it)
                .centerCrop()
                .into(binding.ivAddphoto)
        }
    }
}