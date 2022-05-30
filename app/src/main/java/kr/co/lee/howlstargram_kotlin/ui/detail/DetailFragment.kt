package kr.co.lee.howlstargram_kotlin.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentDetailBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.ui.comment.CommentActivity
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {

    @Inject
    lateinit var fireStore: FirebaseFirestore
    lateinit var adapter: DetailRecyclerAdapter
    lateinit var navController: NavController
    private val detailViewModel: DetailViewModel by viewModels()

    override fun initView() {
        init()
    }

    // 전체 초기화
    private fun init() {
        navController = Navigation.findNavController(binding.recyclerView)
        setAdapter()
        setToolbar()
        detailViewModel.loadImages()
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
                val action = DetailFragmentDirections.detailToBottomSheet()
                navController.navigate(action)
            }

            R.id.action_activity -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    // RecyclerView Adapter 초기화
    private fun setAdapter() {
        adapter = DetailRecyclerAdapter(detailViewModel)
        // 클릭 리스너
        adapter.setOnClickListener(
            object : DetailRecyclerAdapter.FavoriteClickListener {
                override fun click(contentUid: String) {
                    detailViewModel.setFavoriteEvent(contentUid = contentUid)
                }
            },
            object : DetailRecyclerAdapter.CommentClickListener {
                override fun click(content: Content) {
                    startCommentIntent(content = content)
                }
            }, object : DetailRecyclerAdapter.ProfileClickListener {
                override fun click(destinationUid: String, userId: String, profileUrl: String) {
                    val action = DetailFragmentDirections.detailToUser(userId = userId, destinationUid = destinationUid, profileUrl = profileUrl)
                    navController.navigate(action)
                }
            }, object : DetailRecyclerAdapter.LikeClickListener {
                override fun click(favorites: Map<String, Boolean>) {
                    val bundle = bundleOf(
                        "favorites" to favorites
                    )

                    navController.navigate(R.id.detail_to_user, bundle)
                }
            })
    }

    // Intent 시작 메서드 -> 댓글 액티비티
    private fun startCommentIntent(content: Content) {
        val intent = Intent(requireContext(), CommentActivity::class.java)
        intent.putExtra("content", content)
        startActivity(intent)
    }

    // LiveData 관찰
    private fun observeLiveData() {
        detailViewModel.contents.observe(this) {
            adapter.setItems(it)
            binding.recyclerView.adapter = adapter
        }
    }
}