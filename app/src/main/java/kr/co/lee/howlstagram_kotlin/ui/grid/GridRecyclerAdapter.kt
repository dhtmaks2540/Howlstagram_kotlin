package kr.co.lee.howlstagram_kotlin.ui.grid

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kr.co.lee.howlstagram_kotlin.model.Content
import kr.co.lee.howlstagram_kotlin.model.ContentDTO

class GridRecyclerAdapter(
    private val widthPixels: Int,
    private val imageItemClick: (contentDTO: ContentDTO, contentUid: String) -> Unit,
) : ListAdapter<Content, GridRecyclerAdapter.ViewHolder>(diffCallback)
{
    private lateinit var imageView: AppCompatImageView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        imageView = AppCompatImageView(parent.context)
        imageView.layoutParams = LinearLayout.LayoutParams(widthPixels - 10, widthPixels)
        return ViewHolder(imageView).apply {
            imageView.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                imageItemClick(
                    getItem(position).contentDTO!!,
                    getItem(position).contentUid!!
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val imageView: AppCompatImageView): RecyclerView.ViewHolder(imageView) {
        fun bind(content: Content) {
            Glide.with(itemView.context)
                .load(content.contentDTO?.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Content>() {
            override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean =
                oldItem.contentUid == newItem.contentUid

            override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean =
                oldItem == newItem
        }
    }
}
