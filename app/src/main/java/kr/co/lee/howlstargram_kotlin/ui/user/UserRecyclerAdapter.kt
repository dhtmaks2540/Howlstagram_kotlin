package kr.co.lee.howlstargram_kotlin.ui.user

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kr.co.lee.howlstargram_kotlin.model.ContentDTO

class UserRecyclerAdapter(
    private val widthPixels: Int
): ListAdapter<ContentDTO, UserRecyclerAdapter.ViewHolder>(diffUtil) {
    private lateinit var imageView: AppCompatImageView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        imageView = AppCompatImageView(parent.context)
        imageView.layoutParams = LinearLayout.LayoutParams(widthPixels, widthPixels)
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val imageView: AppCompatImageView): RecyclerView.ViewHolder(imageView) {
        fun bind(contentDTO: ContentDTO) {
            Glide.with(itemView.context)
                .load(contentDTO.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ContentDTO>() {
            override fun areItemsTheSame(oldItem: ContentDTO, newItem: ContentDTO): Boolean =
                oldItem.uid == newItem.uid

            override fun areContentsTheSame(oldItem: ContentDTO, newItem: ContentDTO): Boolean =
                oldItem == newItem
        }
    }
}