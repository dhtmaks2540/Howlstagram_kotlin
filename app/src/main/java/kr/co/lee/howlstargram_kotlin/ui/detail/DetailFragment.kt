package kr.co.lee.howlstargram_kotlin.ui.detail

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentDetailBinding
import kr.co.lee.howlstargram_kotlin.ui.like.LikeFragmentDirections
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding, DetailViewModel>(R.layout.fragment_detail) {
    override val viewModel: DetailViewModel by viewModels()
    private lateinit var navController: NavController
    private val detailAdapter: DetailRecyclerAdapter by lazy {
        DetailRecyclerAdapter(
            currentUserUid = viewModel.currentUserUid,
            favoriteItemClicked = { contentUid ->
                lifecycleScope.launch {
                    viewModel.setFavoriteEvent(contentUid)
                }
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

    // 전체 초기화
    private fun init() {
        navController = findNavController()
        binding.apply {
            vm = viewModel
            adapter = detailAdapter

            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    val job = viewModel.refresh()
                    job.join()
                    refreshLayout.isRefreshing = false
                }
            }
        }
        setToolbar()
    }

    // 툴바 지정
    private fun setToolbar() {
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
    }
}
