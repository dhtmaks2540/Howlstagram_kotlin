package kr.co.lee.howlstargram_kotlin.ui.like

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemLikeBinding
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.utilites.FollowClickListener
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener

class LikeRecyclerAdapter(private val currentUserUid: String) : ListAdapter<FavoriteDTO, LikeRecyclerAdapter.ViewHolder>(LikeDiffCallback())
//    : RecyclerView.Adapter<LikeRecyclerAdapter.ViewHolder>()
{
    private lateinit var profileClickListener: ProfileClickListener
    private lateinit var followClickListener: FollowClickListener
    private lateinit var uid: String

//    fun setItems(favoriteDTOs: List<FavoriteDTO>, uid: String) {
//        this.favoriteDTOs = favoriteDTOs
//        this.uid = uid
//        notifyDataSetChanged()
//    }

    fun setOnClickListener(profileClickListener: ProfileClickListener, followClickListener: FollowClickListener) {
        this.profileClickListener = profileClickListener
        this.followClickListener = followClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), currentUserUid)
    }

//    override fun getItemCount(): Int = favoriteDTOs.size

    inner class ViewHolder(private val binding: ItemLikeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                // 프로필 이미지 클릭
                ivProfile.setOnClickListener {
                    profileClickListener.click(getItem(adapterPosition).userUid)
                }

                // 닉네임 클릭
                tvUserNickname.setOnClickListener {
                    profileClickListener.click(getItem(adapterPosition).userUid,)
                }

                // 유저 이름 클릭
                tvUserName.setOnClickListener {
                    profileClickListener.click(getItem(adapterPosition).userUid,)
                }

                // 팔로우 버튼 클릭
                btnFollow.setOnClickListener {
                    followClickListener.followClick(
                        userUid = getItem(adapterPosition).userUid,
                        position = adapterPosition
                    )
                }
            }
        }

        fun bind(item: FavoriteDTO, uid: String) {
            binding.apply {
                uidItem = uid != item.userUid
                favoriteDTOItem = item
            }
        }
    }
}

private class LikeDiffCallback : DiffUtil.ItemCallback<FavoriteDTO>() {
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
