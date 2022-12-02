package kr.co.lee.howlstagram_kotlin.ui.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstagram_kotlin.R
import kr.co.lee.howlstagram_kotlin.base.BaseActivity
import kr.co.lee.howlstagram_kotlin.databinding.ActivityMainBinding
import kr.co.lee.howlstagram_kotlin.utilites.setupWithNavController

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            vm = viewModel
        }

        if(savedInstanceState == null)
            init()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        init()
    }

    // 초기화
    private fun init() {
        val graphIds = listOf(R.navigation.detail, R.navigation.grid, R.navigation.user)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bnv_main)
        val controller = bottomNav.setupWithNavController(
            graphIds,
            supportFragmentManager,
            R.id.fcv_main_content,
            intent
        )

        viewModel.setController(controller)

        viewModel.navController.observe(this) {
            it.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.screen_comment -> {
                        binding.bnvMain.visibility = View.GONE
                    }
                    else -> {
                        binding.bnvMain.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return viewModel.navController.value?.navigateUp()!! || super.onSupportNavigateUp()
    }
}
