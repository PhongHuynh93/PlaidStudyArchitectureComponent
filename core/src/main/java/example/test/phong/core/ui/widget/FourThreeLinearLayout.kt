package example.test.phong.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View.MeasureSpec.*

/**
 * a extension of ForegroundLinearLayout that is always 4:3 aspect radio
 */
class FourThreeLinearLayout(context: Context, attributes: AttributeSet): ForegroundLinearLayout(context, attributes) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val fourThreeHeight = makeMeasureSpec(getSize(widthMeasureSpec) * 3 / 4, EXACTLY)
        super.onMeasure(widthMeasureSpec, fourThreeHeight)
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}