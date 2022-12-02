package kr.co.lee.howlstagram_kotlin.ui.gallerybottomsheet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.co.lee.howlstagram_kotlin.R
import kr.co.lee.howlstagram_kotlin.databinding.FragmentAddBottomSheetBinding
import kr.co.lee.howlstagram_kotlin.ui.gallery.GalleryActivity
import kr.co.lee.howlstagram_kotlin.utilites.GalleryImageType
import kr.co.lee.howlstagram_kotlin.utilites.IMAGE_TYPE
import kr.co.lee.howlstagram_kotlin.utilites.ImageType

@AndroidEntryPoint
class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val itemList = listOf(GalleryImageType.PHOTO, GalleryImageType.STORY)
    private val recyclerAdapter: BottomSheetRecyclerAdapter by lazy {
        BottomSheetRecyclerAdapter(
            bottomItemClicked = { galleryImageType ->
                when(galleryImageType) {
                    GalleryImageType.PHOTO -> {
                        val intent = Intent(requireContext(), GalleryActivity::class.java)
                        intent.putExtra(IMAGE_TYPE, ImageType.POST_TYPE)
                        startActivity(intent)
                        this@BottomSheetDialogFragment.dismiss()
                    }
                    GalleryImageType.STORY -> {
                        Toast.makeText(activity, "현재 구현되지 않은 기능입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
    private var _binding: FragmentAddBottomSheetBinding? = null
    private val binding: FragmentAddBottomSheetBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_bottom_sheet, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            adapter = recyclerAdapter
        }

        val decoration = DividerItemDecoration(activity, VERTICAL)
        recyclerAdapter.submitList(itemList)
        binding.rvMake.addItemDecoration(decoration)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}