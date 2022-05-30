package kr.co.lee.howlstargram_kotlin.ui.like

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentLikeBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity

@AndroidEntryPoint
class LikeFragment: BaseFragment<FragmentLikeBinding>(R.layout.fragment_like) {

    private val likeViewModel: LikeViewModel by viewModels()
    private lateinit var recyclerAdapter: LikeRecyclerAdapter

    override fun initView() {
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        setAdapter()
        setToolbar()
        getBundleData()
        observeLiveData()
    }

    // RecyclerAdapter 설정
    private fun setAdapter() {
        recyclerAdapter = LikeRecyclerAdapter()
        recyclerAdapter.setOnClickListener(object : UserClickListener {
            override fun userClick(userId: String, destinationUid: String, profileUrl: String) {
                val action = LikeFragmentDirections.detailToUser(userId = userId, destinationUid = destinationUid, profileUrl = profileUrl)
                findNavController().navigate(action)
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
        val favorites = (arguments?.getSerializable("favorites") as? Map<String, Boolean>)
        likeViewModel.setFavorites(favorites = favorites)
    }

    // LiveData 관찰
    private fun observeLiveData() {
        likeViewModel.favorites.observe(this) {
             likeViewModel.loadFavorite(it)
        }

        likeViewModel.favoriteDTOs.observe(this) {
            recyclerAdapter.setItems(it, likeViewModel.uid.value.toString())
            binding.recyclerView.adapter = recyclerAdapter
        }
    }
}