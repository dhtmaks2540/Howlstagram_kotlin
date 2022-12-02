package kr.co.lee.howlstagram_kotlin.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstagram_kotlin.databinding.ItemCommentBinding
import kr.co.lee.howlstagram_kotlin.model.Comment

class CommentRecyclerAdapter(
    private val profileItemClicked: (String) -> Unit,
) : ListAdapter<Comment, CommentRecyclerAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding).apply {
            binding.layoutComment.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                profileItemClicked(
                    getItem(position).commentDTO?.uid!!
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    fun addComment(comment: Comment?) {
        val newCommentList = currentList.toMutableList()
        newCommentList.add(1, comment)
        submitList(newCommentList)
    }

    class ViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(comment: Comment) {
            binding.apply {
                commentItem = comment
                executePendingBindings()
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.commentUid == newItem.commentUid

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.commentDTO == newItem.commentDTO
        }
    }
}
