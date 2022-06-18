package kr.co.lee.howlstargram_kotlin.ui.grid

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.utilites.ImageClickListener

class GridRecyclerAdapter(private val widthPixels: Int)
    : ListAdapter<Content, GridRecyclerAdapter.ViewHolder>(ContentDiffCallback())
//    : RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder>()
{
//    private lateinit var contents: List<Content>
    private lateinit var imageView: AppCompatImageView
    private lateinit var imageClickListener: ImageClickListener

//    fun setItems(contents: List<Content>) {
//        this.contents = contents
//        notifyDataSetChanged()
//    }

    fun setClickListener(imageClickListener: ImageClickListener) {
        this.imageClickListener = imageClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        imageView = AppCompatImageView(parent.context)
        imageView.layoutParams = LinearLayout.LayoutParams(widthPixels, widthPixels)
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    override fun getItemCount(): Int = contents.size

    inner class ViewHolder(private val imageView: AppCompatImageView): RecyclerView.ViewHolder(imageView) {
        init {
            imageView.setOnClickListener {
                imageClickListener.click(getItem(adapterPosition).contentDTO, getItem(adapterPosition).contentUid)
            }
        }

        fun bind(content: Content) {
            Glide.with(itemView.context)
                .load(content.contentDTO?.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
        }
    }
}

private class ContentDiffCallback : DiffUtil.ItemCallback<Content>() {
    override fun areItemsTheSame(
        oldItem: Content,
        newItem: Content
    ): Boolean {
        return oldItem.contentUid == newItem.contentUid
    }

    override fun areContentsTheSame(
        oldItem: Content,
        newItem: Content
    ): Boolean {
        return oldItem == newItem
    }
}
