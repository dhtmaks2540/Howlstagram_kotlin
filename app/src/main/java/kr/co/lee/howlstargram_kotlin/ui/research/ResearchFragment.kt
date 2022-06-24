package kr.co.lee.howlstargram_kotlin.ui.research

import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentResearchBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class ResearchFragment: BaseFragment<FragmentResearchBinding>(R.layout.fragment_research) {
    private val viewModel: ResearchViewModel by viewModels()

    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->
                navController.navigateUp()
        }

        return super.onOptionsItemSelected(item)
    }

    // 초기화 메서드
    private fun init() {
        navController = findNavController()

        binding.apply {
            vm = viewModel
            handler = this@ResearchFragment
        }

        setToolbar()
    }

    // 팔로우 클릭
    fun followClickListener() {
        viewModel.requestFollow()
    }

    // 좋아요 이미지 클릭
    fun favoriteClickListener() {
        viewModel.favoriteEvent()
    }

    // 좋아요 페이지 이동
    fun startFavoriteClickListener() {
        val bundle = bundleOf(
            FAVORITES to viewModel.userAndContent.value?.first?.contentDTO?.favorites
        )

        navController.navigate(R.id.action_to_like, bundle)
    }

    // 프로필 페이지 이동
    fun startProfileClickListener() {
        val action = ResearchFragmentDirections.actionToUser(destinationUid =  viewModel.userAndContent.value?.first?.contentDTO?.uid!!)
        navController.navigate(action)
    }

    // 댓글 페이지 이동
    fun startCommentClickListener() {
        val bundle = bundleOf(
            CONTENT to viewModel.userAndContent.value?.first!!
        )

        navController.navigate(R.id.action_to_comment, bundle)
    }

    // Toolbar 설정
    private fun setToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}