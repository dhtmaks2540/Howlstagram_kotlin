package kr.co.lee.howlstargram_kotlin.ui.follow

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentFollowBinding
import kr.co.lee.howlstargram_kotlin.utilites.FOLLOW
import kr.co.lee.howlstargram_kotlin.utilites.FollowClickListener
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener

@AndroidEntryPoint
class FollowFragment: BaseFragment<FragmentFollowBinding>(R.layout.fragment_follow) {
    private val followViewModel: FollowViewModel by viewModels()

    private lateinit var recyclerAdapter: FollowRecyclerAdapter
    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // 초기화
    private fun init() {
        navController = findNavController()
        getBundleData()
        initAdapter()
        observeLiveData()
    }

    // RecyclerAdapter 초기화
    private fun initAdapter() {
        recyclerAdapter = FollowRecyclerAdapter(followViewModel.currentUserUid)
        binding.recyclerView.adapter = recyclerAdapter

        recyclerAdapter.setOnClickListener(object : ProfileClickListener {
            override fun click(destinationUid: String) {
                val action = FollowFragmentDirections.actionToUser(destinationUid = destinationUid)
                navController.navigate(action)
            }
        }, object: FollowClickListener {
            override fun followClick(userUid: String, position: Int) {
                followViewModel.requestFollow(userUid, position)
            }
        })
    }

    // Bundle Data 획득
    private fun getBundleData() {
        arguments?.getSerializable(FOLLOW)?.let {
            val follow = it as Map<String, Boolean>
            followViewModel.getBundleData(follow)
        }
    }

    // Observe LiveData
    private fun observeLiveData() {
        followViewModel.follow.observe(viewLifecycleOwner) {
            followViewModel.loadFollow(it)
        }

        followViewModel.followDTOs.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it)
        }
    }
}