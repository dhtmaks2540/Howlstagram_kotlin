package kr.co.lee.howlstargram_kotlin.ui.detail

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentDetailBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.ui.like.LikeFragmentDirections
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var navController: NavController
    private val detailAdapter: DetailRecyclerAdapter by lazy {
        DetailRecyclerAdapter(
            currentUserUid = viewModel.currentUserUid,
            favoriteItemClicked = { contentUid, position ->
                viewModel.setFavoriteEvent(contentUid, position)
            },
            commentItemClicked = { content ->
                val bundle = bundleOf(
                    CONTENT to content
                )

                navController.navigate(R.id.action_to_comment, bundle)
            },
            profileItemClicked = { destinationUid ->
                val action = LikeFragmentDirections.actionToUser(destinationUid = destinationUid)
                navController.navigate(action)
            },
            likeItemClicked = { favorites ->
                val bundle = bundleOf(
                    FAVORITES to favorites
                )

                navController.navigate(R.id.action_to_like, bundle)
            }
        )
    }

    override fun initView() {
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                navController.navigate(R.id.action_to_bottom)
            }
        }

        return super.onOptionsItemSelected(item)
    }

//    private suspend fun loadContents() {
//        loadJob?.cancel()
//        loadJob = lifecycleScope.launch {
//            viewModel.getAllContents().collectLatest { state ->
//                when(state) {
//                    is State.Loading -> {
//                        showToast("글을 불러오고 있습니다.")
//                    }
//
//                    is State.Success -> {
//                        val contents = state.data
//                        if(contents.isNullOrEmpty()) {
//                            binding.tvEmpty.visibility = View.VISIBLE
//                        } else {
//                            binding.tvEmpty.visibility = View.INVISIBLE
//                        }
//                        viewModel.setContents(contents)
//                    }
//
//                    is State.Failed -> {
//                        showToast("${state.message}로 인해 글을 불러오는데 실패!!")
//                    }
//                }
//            }
//        }
//    }

    // 전체 초기화
    private fun init() {
        navController = findNavController()
        binding.apply {
            vm = viewModel
            adapter = detailAdapter

            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    refreshLayout.isRefreshing = false
                    val job = viewModel.refresh()
                    job.join()
                    refreshLayout.isRefreshing = false
                }
            }
        }
        setToolbar()
//        observeLiveData()
    }

    // 툴바 지정
    private fun setToolbar() {
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
    }

    // LiveData 관찰
//    private fun observeLiveData() {
//        lifecycleScope.launch {
//            viewModel.test.collect { state ->
//                when(state) {
//                    is State.Loading -> {
//                        println("LOADING!!!")
//                    }
//                    is State.Success -> {
//                        println("DATE!!! ${state.data}")
//                    }
//                    is State.Failed -> {
//                        println("FAILED!!! ${state.message}")
//                    }
//                }
//            }
//        }
//    }
}
