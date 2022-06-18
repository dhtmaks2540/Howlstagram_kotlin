package kr.co.lee.howlstargram_kotlin.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemUserBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener

class SearchRecyclerAdapter : ListAdapter<User, SearchRecyclerAdapter.ViewHolder>(UserDiffCallback())
//    : RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>()
{
    private lateinit var profileClickListener: ProfileClickListener

//    fun setItems(users: List<User>) {
//        this.users = users
//        notifyDataSetChanged()
//    }

    fun setClickListener(profileClickListener: ProfileClickListener) {
        this.profileClickListener = profileClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    override fun getItemCount(): Int = users.size

    inner class ViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                layoutItem.setOnClickListener {
                    profileClickListener.click(getItem(adapterPosition).userUid)
                }
            }
        }

        fun bind(item: User) {
            binding.apply {
                userItem = item
            }
        }
    }
}

private class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(
        oldItem: User,
        newItem: User
    ): Boolean {
        return oldItem.userUid == newItem.userUid
    }

    override fun areContentsTheSame(
        oldItem: User,
        newItem: User
    ): Boolean {
        return oldItem == newItem
    }
}
