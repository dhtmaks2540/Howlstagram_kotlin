package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityGalleryBinding
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import kr.co.lee.howlstargram_kotlin.ui.addphoto.AddPhotoActivity
import kr.co.lee.howlstargram_kotlin.utilites.ImageType
import java.io.File
import java.util.*
import javax.inject.Inject

private const val READ_EXTERNAL_STORAGE_PERMISSION = 10

@AndroidEntryPoint
class GalleryActivity : BaseActivity<ActivityGalleryBinding>(R.layout.activity_gallery) {

    @Inject
    lateinit var adapter: GalleryRecyclerAdapter
    private val galleryViewModel: GalleryViewModel by viewModels()
    private val launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    // Toolbar Menu 초기화
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_menu, menu)
        return true
    }

    // Toolbar Menu 선택
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_next -> {
                // 프로필 업로드
                when(galleryViewModel.imageType.value) {
                    ImageType.PROFILE_TYPE -> {
                        galleryViewModel.addProfile()
                        intent.putExtra("profileUrl", galleryViewModel.currentSelectedImage.value)
                        setResult(RESULT_OK)
                        finish()
                    }
                    ImageType.POST_TYPE -> {
                        // 게시글 업로드
                        val intent = Intent(this, AddPhotoActivity::class.java)
                        intent.putExtra("uri", galleryViewModel.currentSelectedImage.value)
                        launcher.launch(intent)
                    }
                    null -> {
                        showToast("다시 요청해주세요.")
                    }
                }
            }
        }

        return true
    }

    private fun init() {
        binding.apply {
            viewModel = galleryViewModel
            grantPermissionButton.setOnClickListener { openMediaStore() }
        }

        setToolbar()
        setIntentData()
        openMediaStore()
        observeLiveData()
    }

    // Intent Data 셋팅
    private fun setIntentData() {
        (intent.getSerializableExtra("imageType") as? ImageType)?.let {
            galleryViewModel.setImageType(it)
        }

    }

    // 툴바 설정
    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_close_black_20)
    }

    // 이미지 호출
    private fun showImages() {
        galleryViewModel.loadImages()
        binding.ivSelect.visibility = View.VISIBLE
        binding.grantPermissionButton.visibility = View.GONE
    }

    // 권한 체크
    private fun openMediaStore() {
        if(checkPermission()) {
            showImages()
        } else {
            requestPermission()
        }
    }

    // LiveData 관찰
    private fun observeLiveData() {
        galleryViewModel.imageList.observe(this) { galleryImageList ->
            adapter.setImageList(galleryImageList, galleryViewModel, this)
            binding.recyclerView.adapter = adapter
        }

        galleryViewModel.currentSelectedImage.observe(this) { uri ->
            Glide.with(binding.ivSelect.context)
                .load(uri)
                .centerCrop()
                .into(binding.ivSelect)
        }
    }

    // 권한 체크 후 요청
    private fun requestPermission() {
        if(!checkPermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_PERMISSION)
        }
    }

    // 권한 체크
    private fun checkPermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    // 권한 없을 경우 보이는 화면
    private fun showNoAccess() {
        binding.ivSelect.visibility = View.INVISIBLE
        binding.grantPermissionButton.visibility = View.VISIBLE
    }

    private fun goToSetting() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImages()
                } else {
                    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                    if(showRationale) {
                        showNoAccess()
                    } else {
                        goToSetting()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}