package kr.co.lee.howlstargram_kotlin.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.lee.howlstargram_kotlin.databinding.ItemDetailBinding
import kr.co.lee.howlstargram_kotlin.model.Content
import javax.inject.Inject

class DetailRecyclerAdapter @Inject constructor(
    private val detailViewModel: DetailViewModel)
    : RecyclerView.Adapter<DetailRecyclerAdapter.ViewHolder>() {

    private lateinit var contents: List<Content>
    private lateinit var favoriteClickListener: FavoriteClickListener
    private lateinit var commentClickListener: CommentClickListener
    private lateinit var profileClickListener: ProfileClickListener

    fun setItems(contents: List<Content>) {
        this.contents = contents
        notifyDataSetChanged()
    }

    fun setOnClickListener(favoriteClickListener: FavoriteClickListener, commentClickListener: CommentClickListener, profileClickListener: ProfileClickListener) {
        this.favoriteClickListener = favoriteClickListener
        this.commentClickListener = commentClickListener
        this.profileClickListener = profileClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(contents[position])
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    inner class ViewHolder(val binding: ItemDetailBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            // 좋아요 사진 클릭
            binding.ivDetailviewitemFavorite.setOnClickListener {
                favoriteClickListener.click(contents[adapterPosition].contentUid!!)
                updateFavorite()
            }

            // 댓글 사진 클릭(to CommentActivity)
            binding.ivDetailviewitemComment.setOnClickListener {
                commentClickListener.click(contents[adapterPosition])
            }

            // 댓글 텍스트 클릭(to CommentActivity)
            binding.tvDetailviewitemComment.setOnClickListener {
                commentClickListener.click(contents[adapterPosition])
            }
            
            // 내용 클릭(to CommentActivity)
            binding.tvDetailviewitemExplain.setOnClickListener {
                commentClickListener.click(contents[adapterPosition])
            }

            // 프로필 이미지 클릭
            binding.ivDetailviewitemProfile.setOnClickListener {
                profileClickListener.click(contents[adapterPosition].contentDTO?.uid!!, contents[adapterPosition].contentDTO?.userId!!, contents[adapterPosition].profileUrl!!)
            }
        }

        fun bind(content: Content) {
            binding.apply {
                viewModel = detailViewModel
                contentItem = content
                executePendingBindings()
            }
        }

        // 좋아요 UI 업데이트
        private fun updateFavorite() {
            CoroutineScope(Dispatchers.Main).launch {
                if(contents[adapterPosition].contentDTO?.favorites?.containsKey(detailViewModel.uid.value)!!) {
                    contents[adapterPosition].contentDTO?.favoriteCount = contents[adapterPosition].contentDTO?.favoriteCount?.minus(1)!!
                    contents[adapterPosition].contentDTO?.favorites?.remove(detailViewModel.uid.value)

                } else {
                    contents[adapterPosition].contentDTO?.favoriteCount = contents[adapterPosition].contentDTO?.favoriteCount?.plus(1)!!
                    contents[adapterPosition].contentDTO?.favorites?.set(detailViewModel.uid.value!!, true)
                }

                notifyItemChanged(adapterPosition)
            }
        }
    }

    // 좋아요 인터페이스
    interface FavoriteClickListener {
        fun click(contentUid: String)
    }

    interface CommentClickListener {
        fun click(content: Content)
    }

    interface ProfileClickListener {
        fun click(destinationUid: String, userId: String, profileUrl: String)
    }
}