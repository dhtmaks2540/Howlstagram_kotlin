package kr.co.lee.howlstagram_kotlin.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lee.howlstagram_kotlin.databinding.ItemDetailBinding
import kr.co.lee.howlstagram_kotlin.model.Content

class DetailRecyclerAdapter(
    private val currentUserUid: String,
    private val favoriteItemClicked: (String) -> Unit,
    private val commentItemClicked: (Content) -> Unit,
    private val profileItemClicked: (String) -> Unit,
    private val likeItemClicked: (Map<String, Boolean>) -> Unit,
) : ListAdapter<Content, DetailRecyclerAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDetailBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding).apply {
            binding.ivDetailviewitemFavorite.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                favoriteItemClicked(
                    getItem(position).contentUid!!
                )
                
                updateFavorite(currentUserUid, position)
            }

            binding.ivDetailviewitemComment.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                commentItemClicked(
                    getItem(position)
                )
            }

            binding.tvDetailviewitemComment.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                commentItemClicked(
                    getItem(position)
                )
            }

            binding.tvDetailviewitemExplain.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                commentItemClicked(
                    getItem(position)
                )
            }

            binding.ivDetailviewitemProfile.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                profileItemClicked(
                    getItem(position).contentDTO?.uid!!
                )
            }

            binding.tvDetailviewitemProfile.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                profileItemClicked(
                    getItem(position).contentDTO?.uid!!
                )
            }

            binding.tvDetailviewitemFavoritecounter.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener

                likeItemClicked(
                    getItem(position).contentDTO?.favorites!!
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), currentUserUid)
    }

    // 좋아요 UI 업데이트
    private fun updateFavorite(uid: String, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (getItem(position).contentDTO?.favorites?.containsKey(uid)!!) {
                getItem(position).contentDTO?.favoriteCount = getItem(position).contentDTO?.favoriteCount?.minus(1)!!
                getItem(position).contentDTO?.favorites?.remove(uid)
            } else {
                getItem(position).contentDTO?.favoriteCount = getItem(position).contentDTO?.favoriteCount?.plus(1)!!
                getItem(position).contentDTO?.favorites?.set(uid, true)
            }
            notifyItemChanged(position)
        }
    }

    class ViewHolder(val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Content, currentUserUid: String) {
            binding.apply {
                currentUserIdItem = currentUserUid
                content = item
                executePendingBindings()
            }
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
