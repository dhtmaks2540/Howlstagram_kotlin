package kr.co.lee.howlstargram_kotlin.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityMainBinding
import kr.co.lee.howlstargram_kotlin.ui.addphoto.AddPhotoActivity
import kr.co.lee.howlstargram_kotlin.ui.alarm.AlarmFragment
import kr.co.lee.howlstargram_kotlin.ui.detail.DetailViewFragment
import kr.co.lee.howlstargram_kotlin.ui.grid.GridFragment
import kr.co.lee.howlstargram_kotlin.ui.user.UserFragment
import kr.co.lee.howlstargram_kotlin.utilites.PageType
import kr.co.lee.howlstargram_kotlin.utilites.READ_STORAGE

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewModel = mainViewModel
        }

        init()
    }

    // 초기화
    private fun init() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_STORAGE
        )

        // Navigation Graph, BottomNavigationView 연결
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_main_content) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnvMain.setupWithNavController(navController)
    }
}