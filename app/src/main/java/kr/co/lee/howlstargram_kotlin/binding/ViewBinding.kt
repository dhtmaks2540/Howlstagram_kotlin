package kr.co.lee.howlstargram_kotlin.binding

import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.lee.howlstargram_kotlin.model.*
import kr.co.lee.howlstargram_kotlin.ui.comment.CommentRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.detail.DetailRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.follow.FollowRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.gallery.GalleryRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.grid.GridRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.like.LikeRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.ui.search.SearchRecyclerAdapter
import kr.co.lee.howlstargram_kotlin.utilites.UiState
import kr.co.lee.howlstargram_kotlin.utilites.successOrNull

object ViewBinding {
    @JvmStatic
    @BindingAdapter("search", "searchText")
    fun AppCompatTextView.bindSearch(state: UiState<*>, textInput: String?) {
        visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
        text = "\"$textInput\" 검색중..."
    }

    @JvmStatic
    @BindingAdapter("show")
    fun ProgressBar.bindShow(state: UiState<*>) {
        visibility = if (state is UiState.Loading) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("adapter")
    fun RecyclerView.bindAdapter(adapter: RecyclerView.Adapter<*>) {
        this.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("noFollow")
    fun AppCompatTextView.bindNoFollow(state: UiState<List<Content>>) {
        if (state.successOrNull()?.size ?: 0 == 0) {
            this.visibility = View.VISIBLE
        } else {
            this.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("contentItems")
    @Suppress("UNCHECKED_CAST")
    fun RecyclerView.bindContentItems(state: UiState<List<*>>) {
        when (val boundAdapter = this.adapter) {
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
                boundAdapter.submitList(state.successOrNull() as? List<User>)
            }
            is GalleryRecyclerAdapter -> {
                boundAdapter.submitList(state.successOrNull() as List<GalleryImage>)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("itemDecoration")
    fun RecyclerView.bindItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        if (itemDecorationCount == 0) {
            addItemDecoration(itemDecoration)
        }
    }

    @JvmStatic
    @BindingAdapter("toast")
    fun View.bindToast(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}
