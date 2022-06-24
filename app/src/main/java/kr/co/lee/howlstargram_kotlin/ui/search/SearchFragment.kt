package kr.co.lee.howlstargram_kotlin.ui.search

import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.FragmentSearchBinding
import kr.co.lee.howlstargram_kotlin.ui.main.MainActivity

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModels()
    private val recyclerAdapter: SearchRecyclerAdapter by lazy {
        SearchRecyclerAdapter(
            profileItemClicked = { destinationUid ->
                val action = SearchFragmentDirections.actionToUser(destinationUid)
                navController.navigate(action)
            }
        )
    }

    private lateinit var navController: NavController

    override fun initView() {
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        navController = findNavController()

        binding.apply {
            vm = viewModel
            adapter = recyclerAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                // 변경 전 이벤트
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                // 텍스트 변경 이벤트
                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    text?.let {
                        lifecycleScope.launch {
                            if(it.isBlank()) {
                                binding.recyclerView.visibility = View.INVISIBLE
                            } else {
                                binding.recyclerView.visibility = View.INVISIBLE
                                val job = viewModel.findUsers(it.toString())
                                job.join()
                                binding.recyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                // 모두 변경된 후 이벤트
                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }

        initToolbar()
    }

    // 툴바 설정
    private fun initToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}