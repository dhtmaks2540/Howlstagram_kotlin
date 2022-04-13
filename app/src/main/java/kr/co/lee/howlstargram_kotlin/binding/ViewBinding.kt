package kr.co.lee.howlstargram_kotlin.di

import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

object ViewBinding {
    @JvmStatic
    @BindingAdapter("onNavigationItemSelected")
    fun bindOnNavigationItemSelectedListener(
        view: BottomNavigationView,
        listener: NavigationBarView.OnItemSelectedListener
    ) {
        view.setOnItemSelectedListener(listener)
    }
}
