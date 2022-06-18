package kr.co.lee.howlstargram_kotlin.ui.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemLikeBinding
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.utilites.FollowClickListener
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener

class FollowRecyclerAdapter(private val uid: String) : ListAdapter<FavoriteDTO, FollowRecyclerAdapter.ViewHolder>(FollowDiffCallback())
//    RecyclerView.Adapter<FollowRecyclerAdapter.ViewHolder>()
{
    private lateinit var profileClickListener: ProfileClickListener
    private lateinit var favoriteClickListener: FollowClickListener

//    fun setItems(favoriteDTOs: List<FavoriteDTO>, uid: String) {
//        this.uid = uid
//        this.favoriteDTOs = favoriteDTOs
//        notifyDataSetChanged()
//    }

    // 클릭 리스너
    fun setOnClickListener(profileClickListener: ProfileClickListener, favoriteClickListener: FollowClickListener) {
        this.profileClickListener = profileClickListener
        this.favoriteClickListener = favoriteClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), uid)
    }

//    override fun getItemCount(): Int = favoriteDTOs.size

    inner class ViewHolder(private val binding: ItemLikeBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                binding.ivProfile.setOnClickListener { profileClickListener.click(getItem(adapterPosition).userUid) }
                binding.tvUserNickname.setOnClickListener { profileClickListener.click(getItem(adapterPosition).userUid) }
                binding.tvUserName.setOnClickListener { profileClickListener.click(getItem(adapterPosition).userUid) }
                binding.btnFollow.setOnClickListener { favoriteClickListener.followClick(userUid = getItem(adapterPosition).userUid, position = adapterPosition) }
            }
        }

        fun bind(favoriteDTO: FavoriteDTO, uid: String) {
            binding.apply {
                uidItem = uid != favoriteDTO.userUid
                favoriteDTOItem = favoriteDTO
            }
        }
    }
}

private class FollowDiffCallback : DiffUtil.ItemCallback<FavoriteDTO>() {
    override fun areItemsTheSame(
        oldItem: FavoriteDTO,
        newItem: FavoriteDTO
    ): Boolean {
        return oldItem.userUid == newItem.userUid
    }

    override fun areContentsTheSame(
        oldItem: FavoriteDTO,
        newItem: FavoriteDTO
    ): Boolean {
        return oldItem == newItem
    }
}
