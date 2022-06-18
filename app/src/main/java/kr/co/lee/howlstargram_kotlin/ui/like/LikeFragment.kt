package kr.co.lee.howlstargram_kotlin.ui.like

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentLikeBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity
import kr.co.lee.howlstargram_kotlin.utilites.FAVORITES
import kr.co.lee.howlstargram_kotlin.utilites.FollowClickListener
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener

@AndroidEntryPoint
class LikeFragment: BaseFragment<FragmentLikeBinding>(R.layout.fragment_like) {

    private val likeViewModel: LikeViewModel by viewModels()
    private lateinit var recyclerAdapter: LikeRecyclerAdapter
    private lateinit var navController : NavController

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

    private fun init() {
        navController = findNavController()
        setAdapter()
        setToolbar()
        getBundleData()
        observeLiveData()
    }

    // RecyclerAdapter 설정
    private fun setAdapter() {
        recyclerAdapter = LikeRecyclerAdapter(likeViewModel.currentUserUid)
        binding.recyclerView.adapter = recyclerAdapter

        recyclerAdapter.setOnClickListener(object : ProfileClickListener {
            override fun click(destinationUid: String) {
                val action = LikeFragmentDirections.actionToUser(destinationUid = destinationUid)
                findNavController().navigate(action)
            }
        }, object : FollowClickListener {
            override fun followClick(userUid: String, position: Int) {
                likeViewModel.requestFollow(userUid, position)
            }
        })
    }

    // 툴바 설정
    private fun setToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    // Bundle 데이터 획득
    private fun getBundleData() {
        val favorites = (arguments?.getSerializable(FAVORITES) as? Map<String, Boolean>)
        likeViewModel.setFavorites(favorites = favorites)
    }

    // LiveData 관찰
    private fun observeLiveData() {
        likeViewModel.favorites.observe(viewLifecycleOwner) {
             likeViewModel.loadFavorite(it)
        }

        likeViewModel.favoriteDTOs.observe(viewLifecycleOwner) {
            recyclerAdapter.submitList(it)
        }
    }
}