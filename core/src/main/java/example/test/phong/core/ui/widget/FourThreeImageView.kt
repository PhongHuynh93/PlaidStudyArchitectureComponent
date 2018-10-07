package example.test.phong.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View.MeasureSpec.*

/**
 * a extension of ForegroundImageView that is always 4:3 aspect radio
 */
open class FourThreeImageView(context: Context, attributes: AttributeSet): ForegroundImageView(context, attributes) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val fourThreeHeight = makeMeasureSpec(getSize(widthMeasureSpec) * 3 / 4, EXACTLY)
        super.onMeasure(widthMeasureSpec, fourThreeHeight)
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}