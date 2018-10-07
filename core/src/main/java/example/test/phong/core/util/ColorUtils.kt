package example.test.phong.core.util

import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.IntRange

class ColorUtils {
    companion object {
        @CheckResult
        @ColorInt
        fun modifyAlpha(@ColorInt color: Int,
                        @IntRange(from = 0, to = 255) alpha: Int): Int {
            return color and 0x00ffffff or (alpha shl 24)
        }
    }

}