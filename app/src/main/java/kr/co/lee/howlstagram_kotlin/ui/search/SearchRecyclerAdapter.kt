package kr.co.lee.howlstagram_kotlin.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstagram_kotlin.databinding.ItemUserBinding
import kr.co.lee.howlstagram_kotlin.model.User

class SearchRecyclerAdapter(
    private val profileItemClicked: (String) -> Unit,
) : ListAdapter<User, SearchRecyclerAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding).apply {
            binding.layoutItem.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                profileItemClicked(
                    getItem(position).userUid
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: User) {
            binding.apply {
                userItem = item
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.userUid == newItem.userUid

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    }
}

