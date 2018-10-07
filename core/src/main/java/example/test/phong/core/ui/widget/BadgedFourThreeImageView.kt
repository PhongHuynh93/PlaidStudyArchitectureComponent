package example.test.phong.core.ui.widget

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.SUBPIXEL_TEXT_FLAG
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import example.test.phong.core.R

/**
 * a view group that draw a badge drawable on top of it's contents
 */
class BadgedFourThreeImageView(context: Context, attributeSet: AttributeSet) : FourThreeImageView(context, attributeSet) {
    private val badge: Drawable
    var drawBadge = false
    private var badgeBoundsSet = false
    private val badgeGravity: Int
    private val badgePadding: Int
    private val badgeBounds: Rect
        get() {
            if (!badgeBoundsSet) {
                layoutBadge()
            }
            return badge.bounds
        }

    init {
        badge = GifBadge(context)
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.BadgedImageView, 0, 0)
        badgeGravity = a.getInt(R.styleable.BadgedImageView_badgeGravity, Gravity.END or Gravity
                .BOTTOM)
        badgePadding = a.getDimensionPixelSize(R.styleable.BadgedImageView_badgePadding, 0)
        a.recycle()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (drawBadge) {
            if (!badgeBoundsSet) {
                layoutBadge()
            }
            badge.draw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutBadge()
    }


    private fun layoutBadge() {
        val badBounds = badge.bounds
        Gravity.apply(badgeGravity,
                      badge.intrinsicWidth,
                      badge.intrinsicHeight,
                      Rect(0, 0, width, height),
                      badgePadding,
                      badgePadding,
                      badgeBounds
                     )
        badge.bounds = badBounds
        badgeBoundsSet = true
    }

    fun setBadgeColor(@ColorInt color: Int) {
        badge.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    private class GifBadge(context: Context) : Drawable() {
        private val paint = Paint()

        init {
            if (bitmap == null) {
                val dm = context.resources.displayMetrics
                val density = dm.density
                val scaleDensity = dm.scaledDensity
                val textPaint = TextPaint(ANTI_ALIAS_FLAG or SUBPIXEL_TEXT_FLAG)
                textPaint.typeface = Typeface.create(TYPEFACE, TYPEFACE_STYLE)
                textPaint.textSize = TEXT_SIZE * scaleDensity

                val padding = PADDING * density
                val textBounds = Rect()
                textPaint.getTextBounds(GIF, 0, GIF.length, textBounds)
                val height = padding + textBounds.height() + padding
                val width = padding + textBounds.width() + padding

                bitmap = createBitmap(width.toInt(), height.toInt()).applyCanvas {
                    val backgroundPaint = Paint(ANTI_ALIAS_FLAG)
                    backgroundPaint.color = BACKGROUND_COLOR
                    val cornerRadius = CORNER_RADIUS * density
                    drawRoundRect(0f, 0f, width, height, cornerRadius, cornerRadius, backgroundPaint)
                    textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    drawText(GIF, padding, height - padding, textPaint)
                }
            }
        }

        override fun draw(canvas: Canvas) {
            canvas.drawBitmap(bitmap, bounds.left.toFloat(), bounds.top.toFloat(), paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        companion object {
            private const val GIF = "GIF"
            private const val TEXT_SIZE = 12    // sp
            private const val PADDING = 4       // dp
            private const val CORNER_RADIUS = 2 // dp
            private const val BACKGROUND_COLOR = Color.WHITE
            private const val TYPEFACE = "sans-serif-black"
            private const val TYPEFACE_STYLE = Typeface.NORMAL
            private var bitmap: Bitmap? = null
        }
    }
}