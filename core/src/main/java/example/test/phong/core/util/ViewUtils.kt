package example.test.phong.core.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView

class ViewUtils {
    companion object {
        val IMAGE_ALPHA = AnimUtils.createIntProperty(object: AnimUtils.IntProp<ImageView>("imageAlpha") {
            override fun set(`object`: ImageView, value: Int) {
                `object`.imageAlpha = value
            }

            override fun get(`object`: ImageView): Int {
                return `object`.imageAlpha
            }
        })

        val BACKGROUND_COLOR = AnimUtils.createIntProperty(object: AnimUtils.IntProp<View>("backgroundColor") {
            override fun set(`object`: View, color: Int) {
                `object`.setBackgroundColor(color)
            }

            override fun get(`object`: View): Int {
                val d = `object`.background
                if (d is ColorDrawable) {
                    return d.color
                }
                return Color.TRANSPARENT
            }
        })
    }
}