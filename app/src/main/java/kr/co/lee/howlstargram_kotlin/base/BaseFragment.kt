package kr.co.lee.howlstargram_kotlin.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import kr.co.lee.howlstargram_kotlin.R

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes val layoutRes: Int) : Fragment() {
    protected lateinit var binding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
        super.onViewCreated(view, savedInstanceState)
    }

    abstract fun initView()

    fun removeFragment(fragment: Fragment) {
        var fragment: Fragment? = fragment
        if (fragment != null) {
            val mFragmentManager = activity?.supportFragmentManager
            val mFragmentTransaction = mFragmentManager?.beginTransaction()
            mFragmentTransaction?.remove(fragment)
            mFragmentTransaction?.commit()
            fragment.onDestroy()
            fragment.onDetach()
        }
    }

    fun addFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.add(R.id.fcv_main_content, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun replaceFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fcv_main_content, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}
