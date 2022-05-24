package kr.co.lee.howlstargram_kotlin.ui.grid

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentGridBinding

@AndroidEntryPoint
class GridFragment: BaseFragment<FragmentGridBinding>(R.layout.fragment_grid) {
    private val gridViewModel: GridViewModel by viewModels()
    private lateinit var recyclerAdapter: GridRecyclerAdapter

    override fun initView() {
        recyclerAdapter = GridRecyclerAdapter(resources.displayMetrics.widthPixels / 3)

        init()
    }

    private fun init() {
        gridViewModel.loadContentDTO()
        observeLiveData()
    }

    private fun observeLiveData() {
        gridViewModel.contentDTOs.observe(this) {
            recyclerAdapter.setItems(it)
            binding.rcvGridFragment.adapter = recyclerAdapter
        }
    }
}