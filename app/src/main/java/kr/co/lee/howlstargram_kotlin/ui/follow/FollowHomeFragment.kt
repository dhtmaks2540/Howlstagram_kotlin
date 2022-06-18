package kr.co.lee.howlstargram_kotlin.ui.follow

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentFollowHomeBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.*

@AndroidEntryPoint
class FollowHomeFragment : BaseFragment<FragmentFollowHomeBinding>(R.layout.fragment_follow_home) {
    private val followHomeViewModel: FollowHomeViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun initView() {
        init()
        getBundleData()
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

    // Bundle 획득
    private fun getBundleData() {
        val userNickName = arguments?.getString(USER_NICKNAME)
        val followers = arguments?.getSerializable(FOLLOWER) as? Map<String, Boolean>
        val followings = arguments?.getSerializable(FOLLOWING) as? Map<String, Boolean>
        val tabType = arguments?.getSerializable(TAB_TYPE) as TabType
        followHomeViewModel.getBundleData(followers, followings, tabType, userNickName)
    }

    // Observe LiveData
    private fun observeLiveData() {
        followHomeViewModel.follow.observe(viewLifecycleOwner) {
            viewPagerAdapter = ViewPagerAdapter(it, this)
            binding.pager.adapter = viewPagerAdapter
            when(followHomeViewModel.tabType.value) {
                TabType.FOLLOWER_TAB ->
                    binding.pager.setCurrentItem(0, false)
                TabType.FOLLOWING_TAB ->
                    binding.pager.setCurrentItem(1, false)
            }

            TabLayoutMediator(binding.tab, binding.pager) { tab, position ->
                when(position) {
                    0 -> tab.text = "팔로워 ${it.first.size}"
                    1 -> tab.text = "팔로잉 ${it.second.size}"
                }
            }.attach()
        }
    }
}