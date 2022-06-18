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
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.ui.like.LikeFragmentDirections
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {
    private val detailViewModel: DetailViewModel by viewModels()

    private lateinit var adapter: DetailRecyclerAdapter
    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    // 전체 초기화
    private fun init() {
        navController = findNavController()

        binding.apply {
            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    val job = detailViewModel.loadContents()
                    job.join()
                    refreshLayout.isRefreshing = false
                }
            }
        }

        setAdapter()
        setToolbar()
        detailViewModel.loadContents()
        observeLiveData()
    }

    // 툴바 지정
    private fun setToolbar() {
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
    }

    // 메뉴 초기화
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    // Toolbar 메뉴 선택
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                navController.navigate(R.id.action_to_bottom)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // RecyclerView Adapter 초기화
    private fun setAdapter() {
        adapter = DetailRecyclerAdapter(detailViewModel.currentUserUid)
        binding.recyclerView.adapter = adapter

        adapter.setOnClickListener(
            // 좋아요 버튼 클릭
            object : FavoriteClickListener {
                override fun click(contentUid: String, position: Int) {
                    detailViewModel.setFavoriteEvent(contentUid, position)
                }
            },
            // 댓글 클릭
            object : CommentClickListener {
                override fun click(content: Content) {
                    val bundle = bundleOf(
                        CONTENT to content
                    )

                    navController.navigate(R.id.action_to_comment, bundle)
                }
            },
            // 프로필 클릭
            object : ProfileClickListener {
                override fun click(destinationUid: String) {
                    val action =
                        LikeFragmentDirections.actionToUser(destinationUid = destinationUid)
                    navController.navigate(action)
                }
            },
            // 좋아요 텍스트 클릭
            object : LikeClickListener {
                override fun click(favorites: Map<String, Boolean>) {

                    val bundle = bundleOf(
                        FAVORITES to favorites
                    )

                    navController.navigate(R.id.action_to_like, bundle)
                }
            })
    }

    // LiveData 관찰
    private fun observeLiveData() {
        detailViewModel.contents.observe(viewLifecycleOwner) { result ->
            binding.hasFollowings = !result.isNullOrEmpty()
            adapter.submitList(result)
        }
    }
}
