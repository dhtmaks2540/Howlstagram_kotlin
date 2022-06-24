package kr.co.lee.howlstargram_kotlin.ui.grid

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration : RecyclerView.ItemDecoration() {
    private val whitePaint = Paint()

    init {
        whitePaint.color = WHITE_COLOR
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom: Float = top + HEIGHT
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, whitePaint);
        }
    }

    companion object {
        private const val WHITE_COLOR = Color.WHITE
        private const val HEIGHT = 7.5f
    }
}