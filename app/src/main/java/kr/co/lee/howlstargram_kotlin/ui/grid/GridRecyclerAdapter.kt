package kr.co.lee.howlstargram_kotlin.ui.grid

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kr.co.lee.howlstargram_kotlin.model.ContentDTO

class GridRecyclerAdapter(private val widthPixels: Int): RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder>() {
    private lateinit var contentDTOs: List<ContentDTO>
    private lateinit var imageView: AppCompatImageView

    fun setItems(contentDTOs: List<ContentDTO>) {
        this.contentDTOs = contentDTOs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        imageView = AppCompatImageView(parent.context)
        imageView.layoutParams = LinearLayout.LayoutParams(widthPixels, widthPixels)
        return ViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contentDTOs[position])
    }

    override fun getItemCount(): Int = contentDTOs.size

    inner class ViewHolder(imageView: AppCompatImageView): RecyclerView.ViewHolder(imageView) {
        fun bind(contentDTO: ContentDTO) {
            Glide.with(itemView.context)
                .load(contentDTO.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView)
        }
    }
}