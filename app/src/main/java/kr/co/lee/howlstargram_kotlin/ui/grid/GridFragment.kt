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
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_DTO
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_UID

@AndroidEntryPoint
class GridFragment : BaseFragment<FragmentGridBinding, GridViewModel>(R.layout.fragment_grid) {
    override val viewModel: GridViewModel by viewModels()
    private val recyclerAdapter: GridRecyclerAdapter by lazy {
        GridRecyclerAdapter(
            widthPixels = resources.displayMetrics.widthPixels / 3,
            imageItemClick = { contentDTO, contentUid ->
                val bundle = bundleOf(
                    CONTENT_DTO to contentDTO,
                    CONTENT_UID to contentUid
                )
                navController.navigate(R.id.action_to_research, bundle)
            }
        )
    }
    private val gridItemDecoration: GridItemDecoration by lazy {
        GridItemDecoration()
    }

    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    // 전체 초기화 메서드
    private fun init() {
        navController = findNavController()

        binding.apply {
            vm = viewModel
            adapter = recyclerAdapter
            handler = this@GridFragment
            itemDecoration = gridItemDecoration

            // 새로고침 클릭
            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    val job = viewModel.refresh()
                    job.join()
                    refreshLayout.isRefreshing = false
                }
            }
        }
    }

    fun searchClickListener() {
        navController.navigate(R.id.action_to_search)
    }
}