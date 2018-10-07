package example.test.phong.core.ui.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class GridItemDividerDecoration (@Dimension private val dividerSize: Int, @ColorInt dividerColor: Int): RecyclerView.ItemDecoration() {
    constructor(context: Context, @DimenRes dividerSizeResId: Int, @ColorRes dividerColorResId: Int)
            : this(context.resources.getDimensionPixelSize(dividerSizeResId), ContextCompat.getColor(context, dividerColorResId))

    private var paint: Paint = Paint().apply {
        color = dividerColor
        style = Paint.Style.FILL
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (parent.isAnimating) return

        val childCount = parent.childCount
        val lm  = parent.layoutManager ?: return
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val holder = parent.getChildViewHolder(child)
            if (holder is Divided) {
                val right = lm.getDecoratedRight(child) as Float
                val bottom = lm.getDecoratedBottom(child) as Float
                c.drawRect(lm.getDecoratedLeft(child).toFloat(), bottom - dividerSize, right, bottom, paint)
                c.drawRect(right - dividerSize, lm.getDecoratedTop(child).toFloat(), right, bottom - dividerSize, paint)
            }
        }
    }
}