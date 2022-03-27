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

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewModel = mainViewModel
        }

        observeLiveData()
        init()
    }

    // 초기화
    private fun init() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE)

        // Navigation Graph, BottomNavigationView 연결
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_main_content) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnvMain.setupWithNavController(navController)
    }

    // LiveDate Observe
    private fun observeLiveData() {
//        mainViewModel.currentPageType.observe(this) {
//            changeFragment(it)
//        }
    }

    // 현재 Fragment는 show, 나머지는 hide
    private fun changeFragment(pageType: PageType) {
        if(pageType != PageType.PAGE3) {
            // 현재 Fragment
            var targetFragment = supportFragmentManager.findFragmentByTag(pageType.pageTag)

            // Fragment KTX의 함수
            supportFragmentManager.commit {
                // 현재 Fragment가 null이라면
                if(targetFragment == null) {
                    // Fragment 획득
                    targetFragment = getFragment(pageType)
                    // 현재 Fragment 추가
                    add(R.id.fcv_main_content, targetFragment!!, pageType.pageTag)
                }

                // 현재 Fragment Show
                show(targetFragment!!)

                // 나머지 Fragment hide
                PageType.values()
                    .filterNot { it == pageType }
                    .forEach { type ->
                        supportFragmentManager.findFragmentByTag(type.pageTag)?.let {
                            hide(it)
                        }
                    }
            }
        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, AddPhotoActivity::class.java))
            } else {
                showToast("권한이 설정되어 있지 않습니다.")
            }
        }
    }

    // PageType에 따라 Fragment 생성
    private fun getFragment(pageType: PageType): Fragment? {
        return when(pageType) {
            PageType.PAGE1 -> DetailViewFragment()
            PageType.PAGE2 -> GridFragment()
            PageType.PAGE3 -> GridFragment()
            PageType.PAGE4 -> AlarmFragment()
            PageType.PAGE5 -> UserFragment()
        }
    }
}