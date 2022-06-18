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
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener
import okhttp3.internal.wait

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var searchRecyclerAdapter: SearchRecyclerAdapter

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
            etSearch.addTextChangedListener(object : TextWatcher {
                // 변경 전 이벤트
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    println("BEFORE TEXT CHANGED : ${p0.toString()}")
                }

                // 텍스트 변경 이벤트
                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    text?.let {
                        if (it.isBlank() || it.isEmpty()) {
                            binding.recyclerView.visibility = View.INVISIBLE
                            binding.labelWait.visibility = View.GONE
                        } else {
                            lifecycleScope.launch {
                                val job = searchViewModel.findUsers(it.toString())
                                binding.recyclerView.visibility = View.INVISIBLE
                                binding.labelWait.visibility = View.VISIBLE
                                binding.labelWait.text = "\"$it\" 검색 중..."

                                job.join()

                                binding.labelWait.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                // 모두 변경된 후 이벤트
                override fun afterTextChanged(p0: Editable?) {
                    println("AFTER TEXT CHANGED : ${p0.toString()}")
                }
            })
        }

        initToolbar()
        observeLiveData()
        initAdapter()
    }

    // Adapter 초기화
    private fun initAdapter() {
        searchRecyclerAdapter = SearchRecyclerAdapter()
        binding.recyclerView.adapter = searchRecyclerAdapter

        searchRecyclerAdapter.setClickListener(object : ProfileClickListener {
            override fun click(destinationUid: String) {
                val action = SearchFragmentDirections.actionToUser(destinationUid)
                navController.navigate(action)
            }
        })
    }

    // 툴바 설정
    private fun initToolbar() {
        val mainActivity = (activity as MainActivity)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // LiveData Observe
    private fun observeLiveData() {
        searchViewModel.users.observe(viewLifecycleOwner) {
            println("USERS!! $it")
            searchRecyclerAdapter.submitList(it)
        }
    }
}