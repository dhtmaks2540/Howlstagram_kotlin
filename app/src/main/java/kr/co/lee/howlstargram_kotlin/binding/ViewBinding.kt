package kr.co.lee.howlstargram_kotlin.binding

import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.model.Comment
import kr.co.lee.howlstargram_kotlin.model.Content
import kr.co.lee.howlstargram_kotlin.model.FavoriteDTO
import kr.co.lee.howlstargram_kotlin.model.User
import kr.co.lee.howlstargram_kotlin.ui.comment.CommentRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.detail.DetailRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.follow.FollowRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.grid.GridRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.like.LikeRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.search.SearchRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import kr.co.lee.howlstargram_kotlin.utilites.successOrNull

object ViewBinding {
//    @JvmStatic
//    @BindingAdapter("onNavigationItemSelected")
//    fun bindOnNavigationItemSelectedListener(
//        view: BottomNavigationView,
//        listener: NavigationBarView.OnItemSelectedListener
//    ) {
//        view.setOnItemSelectedListener(listener)
//    }

    @JvmStatic
    @BindingAdapter("search", "searchText")
    fun AppCompatTextView.bindSearch(state: UiState<*>, textInput: String?) {
        visibility = if(state is UiState.Loading) View.VISIBLE else View.GONE
        text = "\"textInput\" 검색중..."
    }

    @JvmStatic
    @BindingAdapter("show")
    fun ProgressBar.bindShow(state: UiState<*>) {
        visibility = if(state is UiState.Loading) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("adapter")
    fun RecyclerView.bindAdapter(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("noFollow")
    fun AppCompatTextView.bindNoFollow(state: UiState<List<Content>>) {
        if(state.successOrNull()?.size ?: 0 == 0) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("contentItems")
    @Suppress("UNCHECKED_CAST")
    fun RecyclerView.bindContentItems(state: UiState<List<*>>) {
        when(val boundAdapter = this.adapter) {
            is DetailRecyclerAdapter -> {
                boundAdapter.submitList(state.successOrNull() as? List<Content>)
            }
            is CommentRecyclerAdapter -> {
                boundAdapter.submitList(state.successOrNull() as? List<Comment>)
            }
            is LikeRecyclerAdapter -> {
                boundAdapter.submitList(state.successOrNull() as? List<FavoriteDTO>)
            }
            is FollowRecyclerAdapter -> {
                boundAdapter.submitList(state.successOrNull() as? List<FavoriteDTO>)
            }
            is GridRecyclerAdapter -> {
                boundAdapter.submitList(state.successOrNull() as? List<Content>)
            }
            is SearchRecyclerAdapter -> {
                println("USER : ${state.successOrNull() as? List<User>}")
                boundAdapter.submitList(state.successOrNull() as? List<User>)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("toast")
    fun View.bindToast(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    @BindingAdapter("addComment")
    fun RecyclerView.bindAddComment(state: UiState<Comment>) {
        val boundAdapter = this.adapter
        if(boundAdapter is CommentRecyclerAdapter) {
            val newCommentList = boundAdapter.currentList.toMutableList()
            newCommentList.add(1, state.successOrNull())
            boundAdapter.submitList(newCommentList)
        }
    }
}
