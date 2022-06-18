package kr.co.lee.howlstargram_kotlin.ui.research

import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentResearchBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.ContentDTO
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_DTO
import kr.co.lee.howlstargram_kotlin.utilites.CONTENT_UID
import kr.co.lee.howlstargram_kotlin.utilites.FAVORITES

@AndroidEntryPoint
class ResearchFragment: BaseFragment<FragmentResearchBinding>(R.layout.fragment_research) {
    private val researchViewModel: ResearchViewModel by viewModels()

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
            viewModel = researchViewModel

            // 팔로우 버튼 클릭
            btnFollow.setOnClickListener {
                researchViewModel.requestFollow()
            }

            // 좋아요 이미지 클릭
            ivItemFavorite.setOnClickListener { 
                researchViewModel.favoriteEvent()
            }

            // 댓글 클릭
            ivItemComment.setOnClickListener {
                startCommentFragment(content = researchViewModel.content.value!!)
            }
            
            // 댓글 클릭
            tvItemComment.setOnClickListener { 
                startCommentFragment(content = researchViewModel.content.value!!)
            }

            // 글 내용 클릭
            tvItemExplain.setOnClickListener {
                startCommentFragment(content = researchViewModel.content.value!!)
            }

            // 프로필 클릭
            ivUserProfile.setOnClickListener {
                val action = ResearchFragmentDirections.actionToUser(destinationUid = researchViewModel.content.value?.contentDTO?.uid!!)
                navController.navigate(action)
            }

            // 닉네임 클릭
            tvUserNickname.setOnClickListener {
                val action = ResearchFragmentDirections.actionToUser(destinationUid = researchViewModel.content.value?.contentDTO?.uid!!)
                navController.navigate(action)
            }

            // 닉네임 클릭
            tvItemName.setOnClickListener {
                val action = ResearchFragmentDirections.actionToUser(destinationUid = researchViewModel.content.value?.contentDTO?.uid!!)
                navController.navigate(action)
            }

            // 좋아요 텍스트 클릭
            tvItemFavoritecounter.setOnClickListener {
                val bundle = bundleOf(
                    FAVORITES to researchViewModel.content.value?.contentDTO?.favorites
                )

                navController.navigate(R.id.action_to_like, bundle)
            }
        }

        getBundleData()
        setToolbar()
    }

    // bundle 데이터 획득
    private fun startCommentFragment(content: Content) {
        val bundle = bundleOf(
            CONTENT to content
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

    // Bundle을 통해 넘어오는 데이터 받기
    private fun getBundleData() {
        val contentDTO = arguments?.getParcelable<ContentDTO>(CONTENT_DTO)
        val contentUid = arguments?.getString(CONTENT_UID)
        researchViewModel.getBundleData(contentDTO, contentUid)
    }
}