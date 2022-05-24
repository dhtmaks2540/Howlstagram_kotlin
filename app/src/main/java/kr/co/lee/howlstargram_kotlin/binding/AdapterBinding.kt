package kr.co.lee.howlstargram_kotlin.binding

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kr.co.lee.howlstargram_kotlin.R
import org.joda.time.DateTime

object AdapterBinding {
    @JvmStatic
    @BindingAdapter("set_glide_image")
    fun setGlideImage(
        imageView: AppCompatImageView,
        url: String
    ) {
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.baseline_account_circle_black_20)
            .centerCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("set_glide_circle_image")
    fun setGlideCircleImage(
        imageView: CircleImageView,
        url: String?
    ) {
        Glide.with(imageView.context)
            .load(url ?: "")
            .error(R.drawable.baseline_account_circle_black_20)
            .centerCrop()
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("set_favorite_image")
    fun setFavoriteImage(
        imageView: AppCompatImageView,
        isContains: Boolean
    ) {
        if (isContains) {
            imageView.setImageResource(R.drawable.ic_favorite)
        } else {
            imageView.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    @JvmStatic
    @BindingAdapter("set_datetime")
    fun setDateTime(
        textView: AppCompatTextView,
        timeStamp: Long
    ) {
        val now = DateTime()
        val postedDate = DateTime(timeStamp)

        if (postedDate.year != now.year) {
            textView.text = "${now.year - postedDate.year}년 전"
        } else {
            if (postedDate.monthOfYear != now.monthOfYear) {
                textView.text = "${now.monthOfYear - postedDate.monthOfYear}달 전"
            } else {
                if (postedDate.dayOfMonth != now.dayOfMonth) {
                    textView.text = "${now.dayOfMonth - postedDate.dayOfMonth}일 전"
                } else {
                    if (postedDate.hourOfDay != now.hourOfDay) {
                        textView.text = "${now.hourOfDay - postedDate.hourOfDay}시간 전"
                    } else {
                        textView.text = "${now.minuteOfHour - postedDate.minuteOfHour}분 전"
                    }
                }
            }
        }

    }
}