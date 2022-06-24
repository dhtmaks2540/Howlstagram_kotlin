package kr.co.lee.howlstargram_kotlin.ui.comment

import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentCommentBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import kr.co.lee.howlstargram_kotlin.utilites.forEachChildView
import kr.co.lee.howlstargram_kotlin.utilites.successOrNull

@AndroidEntryPoint
class CommentFragment : BaseFragment<FragmentCommentBinding>(R.layout.fragment_comment) {
    private val viewModel: CommentViewModel by viewModels()
    private val recyclerAdapter: CommentRecyclerAdapter by lazy {
        CommentRecyclerAdapter(
            profileItemClicked = { destinationUid ->
                val action = CommentFragmentDirections.actionToUser(destinationUid = destinationUid)
                navController.navigate(action)
            }
        )
    }
    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
        }

        return super.onOptionsItemSelected(item)
    }

    // 전체 초기화 메서드
    private fun init() {
        navController = findNavController()

        binding.apply {
            vm = viewModel
            adapter = recyclerAdapter
            handler = this@CommentFragment

            // 새로고침
            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    binding.layoutRoot.forEachChildView { it.isEnabled = false }
                    val job = viewModel.refresh()
                    job.join()
                    binding.refreshLayout.isRefreshing = false
                    binding.layoutRoot.forEachChildView { it.isEnabled = true }
                }
            }
        }

        initToolbar()
    }

    // 업로드 클릭
    fun uploadClickListener() {
        lifecycleScope.launch {
            binding.loadingBar.visibility = View.VISIBLE
            binding.layoutRoot.forEachChildView { it.isEnabled = false }
            viewModel.addComment().collect { state ->
                when (state) {
                    is UiState.Success -> {
                        binding.loadingBar.visibility = View.GONE
                        binding.layoutRoot.forEachChildView { it.isEnabled = true }
                        recyclerAdapter.addComment(state.successOrNull())
                    }
                }
            }

            binding.etCommentMessage.setText("")
        }
    }

    // 툴바 설정
    private fun initToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
