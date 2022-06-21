package kr.co.lee.howlstargram_kotlin.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import kr.co.lee.howlstargram_kotlin.R

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes val layoutRes: Int) : Fragment() {
    protected lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        initView()
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnDestroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnDestroyView")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnPause")
    }

    override fun onStart() {
        super.onStart()
        Log.d("LIFE_CYCLE", "${this.javaClass.name} : OnStart")
    }

    abstract fun initView()

    protected fun showToast(message: String?) {
        Toast.makeText(requireContext(), "$message", Toast.LENGTH_SHORT).show()
    }
}
