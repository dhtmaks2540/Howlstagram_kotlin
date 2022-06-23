package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.co.lee.howlstargram_kotlin.databinding.ItemGalleryImageBinding
import kr.co.lee.howlstargram_kotlin.model.GalleryImage

class GalleryRecyclerAdapter(
    private val lifeCycle: LifecycleOwner,
    private val viewModel: GalleryViewModel,
    private val imageItemClicked: (String) -> Unit,
) : ListAdapter<GalleryImage, GalleryRecyclerAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGalleryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, lifeCycle).apply {
            binding.ivGallery.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
                imageItemClicked(
                    getItem(position).uri
                )
            }

            viewModel.setCurrentImage(getItem(0).uri)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class ViewHolder(val binding: ItemGalleryImageBinding, private val lifeCycleOwner: LifecycleOwner)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                vm = viewModel
                lifecycleOwner = this@ViewHolder.lifeCycleOwner
            }
        }

        fun bindTo(image: GalleryImage) {
            Glide.with(binding.ivGallery.context)
                .load(image.uri)
                .centerCrop()
                .into(binding.ivGallery)

            binding.imageItem = image
            binding.executePendingBindings()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<GalleryImage>() {
            override fun areItemsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean =
                oldItem.uri == newItem.uri

            override fun areContentsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean =
                oldItem == newItem
        }
    }
}