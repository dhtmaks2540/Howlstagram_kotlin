package kr.co.lee.howlstagram_kotlin.ui.follow

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstagram_kotlin.R
import kr.co.lee.howlstagram_kotlin.base.BaseFragment
import kr.co.lee.howlstagram_kotlin.databinding.FragmentFollowHomeBinding
import kr.co.lee.howlstagram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstagram_kotlin.utilites.TabType

@AndroidEntryPoint
class FollowHomeFragment : BaseFragment<FragmentFollowHomeBinding>(R.layout.fragment_follow_home) {
    private val viewModel: FollowHomeViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun initView() {
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        binding.apply {
            vm = viewModel
        }

        setToolbar()
        observeLiveData()
    }

    // 툴바 초기화
    private fun setToolbar() {
        setHasOptionsMenu(true)
        val mainActivity = activity as MainActivity
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Observe LiveData
    private fun observeLiveData() {
        viewModel.followerAndFollowing.observe(viewLifecycleOwner) {
            viewPagerAdapter = ViewPagerAdapter(it, this)
            binding.pager.adapter = viewPagerAdapter
            when (viewModel.tabType) {
                TabType.FOLLOWER_TAB ->
                    binding.pager.setCurrentItem(0, false)
                TabType.FOLLOWING_TAB ->
                    binding.pager.setCurrentItem(1, false)
            }

            TabLayoutMediator(binding.tab, binding.pager) { tab, position ->
                when (position) {
                    0 -> tab.text = "팔로워 ${it.follower.size}"
                    1 -> tab.text = "팔로잉 ${it.following.size}"
                }
            }.attach()
        }
    }
}
