package kr.co.lee.imageselectmodule

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.imageselectmodule.databinding.ActivityGalleryBinding

class GalleryActivity : BaseActivity<ActivityGalleryBinding>(R.layout.activity_gallery) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {

        }

        initToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }

        return true
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_close_black_20)
    }
}