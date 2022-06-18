package kr.co.lee.howlstargram_kotlin.ui.grid

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentGridBinding
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_DTO
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_UID
import kr.co.lee.howlstargram_kotlin.utilites.ImageClickListener

@AndroidEntryPoint
class GridFragment : BaseFragment<FragmentGridBinding>(R.layout.fragment_grid) {
    private val gridViewModel: GridViewModel by viewModels()

    private lateinit var recyclerAdapter: GridRecyclerAdapter
    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    // 전체 초기화 메서드
    private fun init() {
        navController = findNavController()

        binding.apply {
            // 검색창 클릭
            etSearch.setOnClickListener {
                navController.navigate(R.id.action_to_search)
            }

            // 새로고침 클릭
            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    val job = gridViewModel.loadContentDTO()
                    job.join()
                    refreshLayout.isRefreshing = false
                }
            }
        }

        initAdapter()
        gridViewModel.loadContentDTO()
        observeLiveData()
    }

    // RecyclerView 초기화
    private fun initAdapter() {
        recyclerAdapter = GridRecyclerAdapter(resources.displayMetrics.widthPixels / 3)
        binding.rcvGridFragment.adapter = recyclerAdapter

        recyclerAdapter.setClickListener(object : ImageClickListener {
            override fun click(contentDTO: ContentDTO?, contentUid: String?) {
                val bundle = bundleOf(
                    CONTENT_DTO to contentDTO,
                    CONTENT_UID to contentUid
                )
                navController.navigate(R.id.action_to_research, bundle)
            }
        })
    }

    // LiveData 관찰
    private fun observeLiveData() {
        gridViewModel.contents.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it)
        }
    }
}