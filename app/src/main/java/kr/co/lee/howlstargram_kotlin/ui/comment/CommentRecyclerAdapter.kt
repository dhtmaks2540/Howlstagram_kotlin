package kr.co.lee.howlstargram_kotlin.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemCommentBinding
import kr.co.lee.howlstargram_kotlin.model.Comment

class CommentRecyclerAdapter: RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>() {
    private lateinit var userClickListener: UserClickListener
    private lateinit var comments: List<Comment>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun setClickListener(userClickListener: UserClickListener) {
        this.userClickListener = userClickListener
    }

    inner class ViewHolder(private val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivCommentviewitemProfile.setOnClickListener {
                userClickListener.click(comments[adapterPosition].commentDTO?.userId!!, comments[adapterPosition].commentDTO?.uid!!, comments[adapterPosition].profileUrl!!)
            }

            binding.tvCommentviewitemProfile.setOnClickListener {
                userClickListener.click(comments[adapterPosition].commentDTO?.userId!!, comments[adapterPosition].commentDTO?.uid!!, comments[adapterPosition].profileUrl!!)
            }
        }

        fun bindTo(comment: Comment) {
            binding.apply {
                commentItem = comment
            }
        }
    }

    fun setItems(comments: List<Comment>) {
        this.comments = comments
        notifyDataSetChanged()
    }

    fun addItems(comment: Comment) {
        val newList = comments.toMutableList()
        newList.add(1, comment)
        this.comments = newList
        notifyItemInserted(1)
    }

    interface UserClickListener {
        fun click(userId: String, destinationUid: String, profileUrl: String)
    }
}
