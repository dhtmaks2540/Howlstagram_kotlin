package kr.co.lee.howlstargram_kotlin.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseActivity<VDB : ViewDataBinding, VM: ViewModel>(@LayoutRes val layoutRes: Int) : AppCompatActivity() {
    /**
     * 바인딩 변수
     */
    protected lateinit var binding: VDB
    protected abstract val viewModel: VM

    private val TAG = this.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 초기화된 layoutResId로 DataBinding 객체 생성
        binding = DataBindingUtil.setContentView(this, layoutRes)

        binding.apply {
            lifecycleOwner = this@BaseActivity
        }
    }

    /**
     * 오류 메시지 출력
     */
    protected fun showToast(message: String?) {
        Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
    }
}
