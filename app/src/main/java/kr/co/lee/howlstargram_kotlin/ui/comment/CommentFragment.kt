package kr.co.lee.howlstargram_kotlin.ui.comment

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.ActivityCommentBinding
import kr.co.lee.howlstargram_kotlin.databinding.FragmentCommentBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

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

            tvUploadComment.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.addComment().collect { state ->
                        when(state) {
                            is UiState.Success -> {
                                recyclerAdapter.addComment(state.successOrNull())
                            }
                        }
                    }

                    etCommentMessage.setText("")
                }
            }
        }

        viewModel.loadMyInfo()
        initToolbar()
    }

    // 툴바 설정
    private fun initToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
