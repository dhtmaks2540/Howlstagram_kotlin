package kr.co.lee.howlstargram_kotlin.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.co.lee.howlstargram_kotlin.databinding.ItemGalleryImageBinding
import kr.co.lee.howlstargram_kotlin.model.GalleryImage
import javax.inject.Inject

class GalleryRecyclerAdapter @Inject constructor()
    : RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder>() {

    private lateinit var imageList: List<GalleryImage>
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var lifeCycle: LifecycleOwner

    fun setImageList(item: List<GalleryImage>, viewModel: GalleryViewModel, lifeCycleOwner: LifecycleOwner) {
        galleryViewModel = viewModel
        imageList = item
        lifeCycle = lifeCycleOwner
        galleryViewModel.setCurrentImage(imageList[0].uri)

        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemGalleryImageBinding, val lifeCycleOwner: LifecycleOwner)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                viewModel = galleryViewModel
                lifecycleOwner = this@ViewHolder.lifeCycleOwner
                ivGallery.setOnClickListener {
                    galleryViewModel.setCurrentImage(imageList[adapterPosition].uri)
                }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGalleryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, lifeCycle)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}