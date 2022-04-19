package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityGalleryBinding
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class GalleryActivity : BaseActivity<ActivityGalleryBinding>(R.layout.activity_gallery) {

    @Inject
    lateinit var adapter: GalleryRecyclerAdapter
    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            viewModel = galleryViewModel
        }

        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_next -> {
                // 글 작성 화면으로
            }
        }

        return true
    }

    private fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_close_black_20)

        getFolderList()
        observeLiveData()
    }

    private fun observeLiveData() {
        galleryViewModel.imageList.observe(this) {
            adapter.setImageList(it, galleryViewModel)
            binding.recyclerView.adapter = adapter
        }

        galleryViewModel.currentSelectedImage.observe(this) {
            Glide.with(binding.ivSelect.context)
                .load(it)
                .centerCrop()
                .into(binding.ivSelect)
        }
    }

    private fun getFolderList(): ArrayList<GalleryImage> {
        val isPermission = checkPermission()
        val imageList = ArrayList<GalleryImage>()
        if (isPermission) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )
            val query = contentResolver.query(uri, projection, null, null, null)
            query?.use { cursor ->
                // Cache Column indices
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val date = cursor.getLong(dateColumn)
                    val contentUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )
                    val galleryImage = GalleryImage(contentUri.toString(), name, date)
                    if (!imageList.contains(galleryImage)) {
                        imageList.add(galleryImage)
                    }
                }
            }
        }
        galleryViewModel.setImageList(imageList)
        return imageList
    }

    private fun checkPermission(): Boolean {
        val isPermission =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return if (isPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    showToast("권한이 허용되지 않아서 이전 화면으로 돌아갑니다.")
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}