package kr.co.lee.howlstagram_kotlin.ui.follow

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstagram_kotlin.R
import kr.co.lee.howlstagram_kotlin.base.BaseFragment
import kr.co.lee.howlstagram_kotlin.databinding.FragmentFollowBinding
import kr.co.lee.howlstagram_kotlin.ui.comment.CommentFragmentDirections
import kr.co.lee.howlstagram_kotlin.utilites.UiState
import kr.co.lee.howlstagram_kotlin.utilites.successOrNull
import kr.co.lee.howlstagram_kotlin.utilites.throwableOrNull

@AndroidEntryPoint
class FollowFragment : BaseFragment<FragmentFollowBinding>(R.layout.fragment_follow) {
    private val viewModel: FollowViewModel by viewModels()
    private val recyclerAdapter: FollowRecyclerAdapter by lazy {
        FollowRecyclerAdapter(
            currentUserUid = viewModel.currentUserUid,
            profileItemClicked = { destinationUid ->
                val action = CommentFragmentDirections.actionToUser(destinationUid = destinationUid)
                navController.navigate(action)
            },
            followItemClicked = { userUid, position ->
                lifecycleScope.launch {
                    viewModel.requestFollow(userUid).collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                recyclerAdapter.changeFavoriteDTOs(
                                    state.successOrNull()!!,
                                    position
                                )
                            }
                            is UiState.Failed -> {
                                showToast("Error Message : ${state.throwableOrNull()}")
                            }
                            else -> {}
                        }
                    }
                }
            }
        )
    }

    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    // 초기화
    private fun init() {
        navController = findNavController()

        binding.apply {
            vm = viewModel
            adapter = recyclerAdapter
        }
    }
}
