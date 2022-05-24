package kr.co.lee.howlstargram_kotlin.ui.like

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemLikeBinding
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO

class LikeRecyclerAdapter: RecyclerView.Adapter<LikeRecyclerAdapter.ViewHolder>() {

    private lateinit var favoriteDTOs: List<FavoriteDTO>
    private lateinit var userClickListener: UserClickListener
    private lateinit var userId: String

    fun setItems(favoriteDTOs: List<FavoriteDTO>, userId: String) {
        this.favoriteDTOs = favoriteDTOs
        this.userId = userId
        notifyDataSetChanged()
    }

    fun setOnClickListener(userClickListener: UserClickListener) {
        this.userClickListener = userClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(favoriteDTOs[position])
    }

    override fun getItemCount(): Int = favoriteDTOs.size

    inner class ViewHolder(private val binding: ItemLikeBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivProfile.setOnClickListener { userClickListener.userClick(favoriteDTOs[adapterPosition].userNickName, favoriteDTOs[adapterPosition].userUid, favoriteDTOs[adapterPosition].profileUrl) }
            binding.tvUserNickname.setOnClickListener { userClickListener.userClick(favoriteDTOs[adapterPosition].userNickName, favoriteDTOs[adapterPosition].userUid, favoriteDTOs[adapterPosition].profileUrl) }
            binding.tvUserName.setOnClickListener { userClickListener.userClick(favoriteDTOs[adapterPosition].userNickName, favoriteDTOs[adapterPosition].userUid, favoriteDTOs[adapterPosition].profileUrl) }
        }

        fun bind(item: FavoriteDTO) {
            binding.apply {
                favoriteDTOItem = item
            }
        }
    }
}

interface UserClickListener {
    fun userClick(userId: String, destinationUid: String, profileUrl: String)
}