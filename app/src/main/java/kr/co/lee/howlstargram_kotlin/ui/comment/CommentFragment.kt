package kr.co.lee.howlstargram_kotlin.ui.comment

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.ActivityCommentBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_UID
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener

@AndroidEntryPoint
class CommentFragment : BaseFragment<ActivityCommentBinding>(R.layout.activity_comment) {

    private val commentViewModel: CommentViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var recyclerAdapter: CommentRecyclerAdapter

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
            viewModel = commentViewModel

            tvUploadComment.setOnClickListener {
                commentViewModel.addComment()
                etCommentMessage.setText("")
            }
        }

        commentViewModel.loadMyInfo()
        setAdapter()
        getBundleData()
        initToolbar()
        observeLiveData()
    }

    // RecyclerView Adapter 초기화
    private fun setAdapter() {
        recyclerAdapter = CommentRecyclerAdapter()
        binding.recyclerview.adapter = recyclerAdapter

        recyclerAdapter.setClickListener(object : ProfileClickListener {
            override fun click(destinationUid: String) {
                val action = CommentFragmentDirections.actionToUser(destinationUid = destinationUid)
                navController.navigate(action)
            }
        })
    }

    // LiveData 관찰
    private fun observeLiveData() {
        commentViewModel.content.observe(viewLifecycleOwner) {
            commentViewModel.loadComments(contentUid = it.contentUid!!)
        }

        commentViewModel.comments.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it)
        }
    }

    // 툴바 설정
    private fun initToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Bundle Data 획득
    private fun getBundleData() {
        val content = arguments?.getParcelable<Content>(CONTENT)
        commentViewModel.setInitData(content = content)
    }
}
