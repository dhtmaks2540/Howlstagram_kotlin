package kr.co.lee.howlstargram_kotlin.ui.gallerybottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemAddBottomSheetBinding
import kr.co.lee.howlstargram_kotlin.utilites.BottomSheetClickListener
import kr.co.lee.howlstargram_kotlin.utilites.GalleryImageType

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private lateinit var bottomSheetClickListener: BottomSheetClickListener
    private val itemList = listOf(GalleryImageType.PHOTO, GalleryImageType.STORY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAddBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun setOnClickListener(bottomSheetClickListener: BottomSheetClickListener) {
        this.bottomSheetClickListener = bottomSheetClickListener
    }

    inner class ViewHolder(val binding: ItemAddBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.layout.setOnClickListener {
                when(itemList[adapterPosition]) {
                    GalleryImageType.PHOTO -> {
                        bottomSheetClickListener.click(GalleryImageType.PHOTO)
                    }
                    GalleryImageType.STORY -> {
                        bottomSheetClickListener.click(GalleryImageType.STORY)
                    }
                }
            }
        }

        fun bind(imageType: GalleryImageType) {
            binding.apply {
                imageTypeItem = imageType
            }
        }
    }
}