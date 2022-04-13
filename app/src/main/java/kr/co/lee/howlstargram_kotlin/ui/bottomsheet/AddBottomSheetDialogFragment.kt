package kr.co.lee.howlstargram_kotlin.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.lee.howlstargram_kotlin.R
import kr.co.lee.howlstargram_kotlin.base.BaseFragment
import kr.co.lee.howlstargram_kotlin.databinding.AddBottomSheetDialogFragmentBinding
import kr.co.lee.howlstargram_kotlin.ui.bottomsheet.adapter.AddBottomSheetDialogAdapter

class AddBottomSheetDialogFragment: BottomSheetDialogFragment() {

    private var _binding: AddBottomSheetDialogFragmentBinding? = null
    private val binding: AddBottomSheetDialogFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddBottomSheetDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        val recyclerAdapter = AddBottomSheetDialogAdapter()
        binding.rvMake.adapter = recyclerAdapter
    }
}