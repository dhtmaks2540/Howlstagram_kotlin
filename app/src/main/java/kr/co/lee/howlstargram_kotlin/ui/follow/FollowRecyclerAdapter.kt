package kr.co.lee.howlstargram_kotlin.ui.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemLikeBinding
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO

class FollowRecyclerAdapter(
    private val currentUserUid: String,
    private val profileItemClicked: (String) -> Unit,
    private val followItemClicked: (String, Int) -> Unit,
) : ListAdapter<FavoriteDTO, FollowRecyclerAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLikeBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding).apply {
            binding.ivProfile.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener

                profileItemClicked(
                    getItem(position).userUid
                )
            }

            binding.tvUserNickname.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener

                profileItemClicked(
                    getItem(position).userUid
                )
            }

            binding.tvUserName.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener

                profileItemClicked(
                    getItem(position).userUid
                )
            }

            binding.btnFollow.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener

                followItemClicked(
                    getItem(position).userUid,
                    position
                )
            }
        }
    }

    fun changeFavoriteDTOs(isFollow: Boolean, position: Int) {
        val newFavoriteDTOs = currentList.toMutableList()
        newFavoriteDTOs[position] = newFavoriteDTOs[position].copy(isFollow = isFollow)
        submitList(newFavoriteDTOs)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), currentUserUid)
    }

    class ViewHolder(private val binding: ItemLikeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favoriteDTO: FavoriteDTO, uid: String) {
            binding.apply {
                uidItem = uid != favoriteDTO.userUid
                favoriteDTOItem = favoriteDTO
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FavoriteDTO>() {
            override fun areItemsTheSame(oldItem: FavoriteDTO, newItem: FavoriteDTO): Boolean =
                oldItem.userUid == newItem.userUid

            override fun areContentsTheSame(oldItem: FavoriteDTO, newItem: FavoriteDTO): Boolean =
                oldItem == newItem
        }
    }
}
