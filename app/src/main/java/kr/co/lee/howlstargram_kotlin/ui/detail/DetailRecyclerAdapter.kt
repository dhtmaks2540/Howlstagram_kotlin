package kr.co.lee.howlstargram_kotlin.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.databinding.ItemDetailBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.utilites.CommentClickListener
import kr.co.lee.howlstargram_kotlin.utilites.FavoriteClickListener
import kr.co.lee.howlstargram_kotlin.utilites.LikeClickListener
import kr.co.lee.howlstargram_kotlin.utilites.ProfileClickListener
import javax.inject.Inject

class DetailRecyclerAdapter @Inject constructor(private val currentUserUid: String)
    : ListAdapter<Content, DetailRecyclerAdapter.ViewHolder>(ContentDiffCallback()) {

    private lateinit var favoriteClickListener: FavoriteClickListener
    private lateinit var commentClickListener: CommentClickListener
    private lateinit var profileClickListener: ProfileClickListener
    private lateinit var likeClickListener: LikeClickListener

    // 클릭 리스너
    fun setOnClickListener(favoriteClickListener: FavoriteClickListener, commentClickListener: CommentClickListener,
                           profileClickListener: ProfileClickListener, likeClickListener: LikeClickListener) {
        this.favoriteClickListener = favoriteClickListener
        this.commentClickListener = commentClickListener
        this.profileClickListener = profileClickListener
        this.likeClickListener = likeClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), currentUserUid)
    }

    inner class ViewHolder(val binding: ItemDetailBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                // 좋아요 사진 클릭
                ivDetailviewitemFavorite.setOnClickListener {
                    favoriteClickListener.click(getItem(adapterPosition).contentUid!!, adapterPosition)
                }

                // 댓글 사진 클릭(to CommentActivity)
                ivDetailviewitemComment.setOnClickListener {
                    commentClickListener.click(getItem(adapterPosition))
                }

                // 댓글 텍스트 클릭(to CommentActivity)
                tvDetailviewitemComment.setOnClickListener {
                    commentClickListener.click(getItem(adapterPosition))
                }

                // 내용 클릭(to CommentActivity)
                tvDetailviewitemExplain.setOnClickListener {
                    commentClickListener.click(getItem(adapterPosition))
                }

                // 프로필 이미지 클릭
                ivDetailviewitemProfile.setOnClickListener {
                    profileClickListener.click(getItem(adapterPosition).contentDTO?.uid!!)
                }

                // 유저 아이디 클릭
                tvDetailviewitemName.setOnClickListener {
                    profileClickListener.click(getItem(adapterPosition).contentDTO?.uid!!)
                }

                // 유저 아이디 클릭
                tvDetailviewitemProfile.setOnClickListener {
                    profileClickListener.click(getItem(adapterPosition).contentDTO?.uid!!)
                }

                // 좋아요 클릭
                tvDetailviewitemFavoritecounter.setOnClickListener {
                    likeClickListener.click(favorites = getItem(adapterPosition).contentDTO?.favorites!!)
                }
            }
        }

        fun bind(content: Content, currentUserUid: String) {
            binding.apply {
                currentUserIdItem = currentUserUid
                contentItem = content
                executePendingBindings()
            }
        }

        // 좋아요 UI 업데이트
//        private fun updateFavorite() {
//            CoroutineScope(Dispatchers.Main).launch {
//                if(contents[adapterPosition].contentDTO?.favorites?.containsKey(detailViewModel.uid.value)!!) {
//                    contents[adapterPosition].contentDTO?.favoriteCount = contents[adapterPosition].contentDTO?.favoriteCount?.minus(1)!!
//                    contents[adapterPosition].contentDTO?.favorites?.remove(detailViewModel.uid.value)
//
//                } else {
//                    contents[adapterPosition].contentDTO?.favoriteCount = contents[adapterPosition].contentDTO?.favoriteCount?.plus(1)!!
//                    contents[adapterPosition].contentDTO?.favorites?.set(detailViewModel.uid.value!!, true)
//                }
//
//                notifyItemChanged(adapterPosition)
//            }
//        }
    }
}

private class ContentDiffCallback : DiffUtil.ItemCallback<Content>() {
    override fun areItemsTheSame(
        oldItem: Content,
        newItem: Content
    ): Boolean {
        return oldItem.contentUid == newItem.contentUid
    }

    override fun areContentsTheSame(
        oldItem: Content,
        newItem: Content
    ): Boolean {
        return oldItem == newItem
    }
}
