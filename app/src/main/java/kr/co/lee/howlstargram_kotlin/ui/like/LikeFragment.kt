package kr.co.lee.howlstargram_kotlin.ui.like

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentLikeBinding
import kr.co.lee.howlstargram_kotlin.ui.comment.CommentFragmentDirections
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class LikeFragment: BaseFragment<FragmentLikeBinding, LikeViewModel>(R.layout.fragment_like) {
    override val viewModel: LikeViewModel by viewModels()
    private val recyclerAdapter: LikeRecyclerAdapter by lazy {
        LikeRecyclerAdapter(
            currentUserUid = viewModel.currentUserUid,
            profileItemClicked = { destinationUid ->
                val action = CommentFragmentDirections.actionToUser(destinationUid = destinationUid)
                navController.navigate(action)
            },
            followItemClicked = { userUid, position ->
                lifecycleScope.launch {
                    viewModel.requestFollow(userUid).collect { state ->
                        when(state) {
                            is UiState.Success -> {
                                recyclerAdapter.changeFavoriteDTOs(state.successOrNull()!!, position)
                            }
                            is UiState.Failed -> {
                                showToast("Error Message : ${state.throwableOrNull()}")
                            }
                        }
                    }
                }
            }
        )
    }
    private lateinit var navController : NavController

    override fun initView() {
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        navController = findNavController()
        setToolbar()
        observeLiveData()

        binding.apply {
            vm = viewModel
            adapter = recyclerAdapter
        }
    }

    // 툴바 설정
    private fun setToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }


    // LiveData 관찰
    private fun observeLiveData() {
        viewModel.favoriteDTOs.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it)
        }
    }
}