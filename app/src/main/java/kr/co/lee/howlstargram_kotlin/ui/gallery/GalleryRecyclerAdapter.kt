package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemGalleryImageBinding
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import javax.inject.Inject

class GalleryRecyclerAdapter @Inject constructor()
    : RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder>() {
    private lateinit var imageList: List<GalleryImage>
    private lateinit var galleryViewModel: GalleryViewModel

    fun setImageList(item: List<GalleryImage>, viewModel: GalleryViewModel) {
        galleryViewModel = viewModel
        imageList = item
        viewModel.setCurrentImage(imageList[0].uri)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemGalleryImageBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                viewModel = galleryViewModel
                ivGallery.setOnClickListener {
                    galleryViewModel.setCurrentImage(imageList[adapterPosition].uri)
                    ivGallery.alpha = 0.5f
                }
            }
        }

        fun bindTo(image: GalleryImage) {
            binding.imageItem = image
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGalleryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}