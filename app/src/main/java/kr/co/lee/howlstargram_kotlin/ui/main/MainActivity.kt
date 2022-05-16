package kr.co.lee.howlstargram_kotlin.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseActivity
import kr.co.lee.howlstargram_kotlin.databinding.ActivityMainBinding
import kr.co.lee.howlstargram_kotlin.utilites.PageType

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            viewModel = mainViewModel
        }

        init()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration)
//    }

    // 초기화
    private fun init() {
        // Navigation Graph, BottomNavigationView 연결
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_main_content) as NavHostFragment
        navController = navHostFragment.navController

        // BottomNavigationView와 navController 연결
        binding.bnvMain.setupWithNavController(navController)

//        appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.screen_detail, R.id.screen_grid, R.id.test_screen, R.id.screen_alarm, R.id.screen_user)
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }

}