package kr.co.lee.howlstagram_kotlin.ui.gallerybottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstagram_kotlin.databinding.ItemAddBottomSheetBinding
import kr.co.lee.howlstagram_kotlin.utilites.GalleryImageType

class BottomSheetRecyclerAdapter(
    private val bottomItemClicked: (GalleryImageType) -> Unit,
) : ListAdapter<GalleryImageType, BottomSheetRecyclerAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            binding.layout.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                bottomItemClicked(
                    getItem(position)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(val binding: ItemAddBottomSheetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageType: GalleryImageType) {
            binding.apply {
                imageTypeItem = imageType
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<GalleryImageType>() {
            override fun areItemsTheSame(oldItem: GalleryImageType, newItem: GalleryImageType): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: GalleryImageType, newItem: GalleryImageType): Boolean =
                oldItem == newItem
        }
    }
}