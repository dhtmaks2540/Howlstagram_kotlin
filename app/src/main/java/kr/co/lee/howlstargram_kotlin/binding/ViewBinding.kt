package kr.co.lee.howlstargram_kotlin.binding

import android.view.View
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

    @JvmStatic
    @BindingAdapter("isGone")
    fun bindIsGone(view: View, isGone: Boolean) {
        view.visibility = if (isGone) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}
