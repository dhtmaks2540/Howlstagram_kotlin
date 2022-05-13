package kr.co.lee.howlstargram_kotlin.ui.detail

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentDetailBinding
import kr.co.lee.howlstargram_kotlin.ui.comment.CommentActivity
import javax.inject.Inject

@AndroidEntryPoint
class DetailViewFragment: BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {

    @Inject
    lateinit var fireStore: FirebaseFirestore
    lateinit var adapter: DetailRecyclerAdapter
    private val detailViewModel: DetailViewModel by viewModels()

    override fun initView() {
        init()
    }

    private fun init() {
        initAdapter()
        detailViewModel.loadImages()
        observeLiveData()
    }

    private fun initAdapter() {
        adapter = DetailRecyclerAdapter(detailViewModel)
        adapter.setOnClickListener(
            object : DetailRecyclerAdapter.FavoriteClickListener {
                override fun click(contentUid: String) {
                    detailViewModel.setFavoriteEvent(contentUid)
                }
        },
            object : DetailRecyclerAdapter.CommentClickListener {
                override fun click(contentUid: String?, destinationUid: String?) {
                    startCommentIntent(contentUid, destinationUid)
                }
        })
    }

    private fun startCommentIntent(contentUid: String?, destinationUid: String?) {
        val intent = Intent(requireContext(), CommentActivity::class.java)
        intent.putExtra("contentUid", contentUid)
        intent.putExtra("destinationUid", destinationUid)
        startActivity(intent)
    }

    private fun observeLiveData() {
        detailViewModel.contents.observe(this) {
            adapter.setItems(it)
            binding.recyclerView.adapter = adapter
        }

//        detailViewModel.pairs.observe(this) {
//            detailViewModel.loadProfileUrls(it.first)
//            detailViewModel.loadComments(it.second)
////            adapter.setItems(it.first, it.second)
////            binding.recyclerView.adapter = adapter
//        }
//
////        detailViewModel.profileUrls.observe(this) { profileUrls ->
////            adapter.setItems(detailViewModel.pairs.value!!.first, detailViewModel.pairs.value!!.second, profileUrls)
////            binding.recyclerView.adapter = adapter
////        }
//
//        detailViewModel.comments.observe(this) { comments ->
//            println("Comments!!! : $comments")
////            adapter.setItems(detailViewModel.pairs.value!!.first, detailViewModel.pairs.value!!.second, detailViewModel.profileUrls.value!!, comments)
////            binding.recyclerView.adapter = adapter
    }
}