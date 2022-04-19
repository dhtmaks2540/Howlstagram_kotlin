package kr.co.lee.howlstargram_kotlin.binding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import kr.co.lee.howlstargram_kotlin.R

object AdapterBinding {
    @JvmStatic
    @BindingAdapter("set_glide_image")
    fun setGlideImage(
        imageView: AppCompatImageView,
        url: String
    ) {
        Glide.with(imageView.context)
            .load(url)
            .centerCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("set_favorite_image")
    fun setFavoriteImage(
        imageView: AppCompatImageView,
        isContains: Boolean
    ) {
        if(isContains) {
            imageView.setImageResource(R.drawable.ic_favorite)
        } else {
            imageView.setImageResource(R.drawable.ic_favorite_border )
        }
    }
}