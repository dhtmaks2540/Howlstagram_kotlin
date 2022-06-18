package kr.co.lee.howlstargram_kotlin.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemCommentBinding
import kr.co.lee.howlstargram_kotlin.model.Comment
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener

class CommentRecyclerAdapter : ListAdapter<Comment, CommentRecyclerAdapter.ViewHolder>(CommentDiffCallback())
{
    private lateinit var profileClickListener: ProfileClickListener

    // 클릭 리스너
    fun setClickListener(profileClickListener: ProfileClickListener) {
        this.profileClickListener = profileClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

//    override fun getItemCount(): Int = comments.size

    inner class ViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.layoutComment.setOnClickListener {
                profileClickListener.click(getItem(adapterPosition).commentDTO?.uid!!)
            }
        }

        fun bindTo(comment: Comment) {
            binding.apply {
                commentItem = comment
                executePendingBindings()
            }
        }
    }
}

private class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(
        oldItem: Comment,
        newItem: Comment
    ): Boolean {
        return oldItem.commentUid == newItem.commentUid
    }

    override fun areContentsTheSame(
        oldItem: Comment,
        newItem: Comment
    ): Boolean {
        return oldItem.commentDTO == newItem.commentDTO
    }
}
