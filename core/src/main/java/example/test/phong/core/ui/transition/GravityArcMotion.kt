package example.test.phong.core.ui.transition

import android.graphics.Path
import android.transition.ArcMotion

/**
 * a tweak to ArcMotion which slightly alters the path calculation. In the real world gravity slows upward and accelerates
 * downward motion. This class emulates this behavior to make motion paths appear more natural
 *
 */
class GravityArcMotion() : ArcMotion() {

    companion object {
        private val DEFAULT_MIN_ANGLE_DEGREES = 0f
        private val DEFAULT_MAX_ANGLE_DEGREES = 70f
        private val DEFAULT_MAX_TANGENT = Math.tan(Math.toRadians((DEFAULT_MAX_ANGLE_DEGREES / 2).toDouble())).toFloat()

        fun toTangent(angleInDegrees: Float): Float {
            if (angleInDegrees < 0 || angleInDegrees > 90) {
                throw IllegalStateException("Arc must be between 0 and 90")
            }
            return Math.tan(Math.toRadians((angleInDegrees / 2f).toDouble())).toFloat()
        }

    }

    private var mMinimumHorizontalAngle = 0f
    private var mMinimumHorizontalTangent = 0f
    private var mMinimumVerticalAngle = 0f
    private var mMinimumVerticalTangent = 0f

    private var mMaximumAngle = DEFAULT_MAX_ANGLE_DEGREES
    private var mMaximumTangent = DEFAULT_MAX_TANGENT

    override fun setMinimumHorizontalAngle(angleInDegrees: Float) {
        mMinimumHorizontalAngle = angleInDegrees
        mMinimumHorizontalTangent = toTangent(angleInDegrees)
    }

    override fun getMinimumHorizontalAngle(): Float {
        return mMinimumHorizontalAngle
    }

    override fun setMinimumVerticalAngle(angleInDegrees: Float) {
        mMinimumVerticalAngle = angleInDegrees
        mMinimumVerticalTangent = toTangent(angleInDegrees)
    }

    override fun getMinimumVerticalAngle(): Float {
        return mMinimumVerticalAngle
    }

    override fun setMaximumAngle(angleInDegrees: Float) {
        mMaximumAngle = angleInDegrees
        mMaximumTangent = toTangent(angleInDegrees)
    }

    override fun getMaximumAngle(): Float {
        return mMaximumAngle
    }


    override fun getPath(startX: Float, startY: Float, endX: Float, endY: Float): Path {
        val path = Path()
        path.moveTo(startX, startY)

        var ex: Float
        var ey: Float
        if (startY == endY) {
            ex = (startX + endX) / 2
            ey = startY + mMinimumHorizontalTangent * Math.abs(endX - startX) / 2
        } else if (startX == endX) {
            ex = startX + mMinimumVerticalTangent * Math.abs(endY - startY) / 2
            ey = (startY + endY) / 2
        } else {
            val deltaX = endX - startX

            /**
             * This is the only change to ArcMotion
             */
            val deltaY: Float
            if (endY < startY) {
                deltaY = startY - endY // Y is inverted compared to diagram above.
            } else {
                deltaY = endY - startY
            }
            /**
             * End changes
             */

            // hypotenuse squared.
            val h2 = deltaX * deltaX + deltaY * deltaY

            // Midpoint between start and end
            val dx = (startX + endX) / 2
            val dy = (startY + endY) / 2

            // Distance squared between end point and mid point is (1/2 hypotenuse)^2
            val midDist2 = h2 * 0.25f

            val minimumArcDist2: Float

            if (Math.abs(deltaX) < Math.abs(deltaY)) {
                // Similar triangles bfa and bde mean that (ab/fb = eb/bd)
                // Therefore, eb = ab * bd / fb
                // ab = hypotenuse
                // bd = hypotenuse/2
                // fb = deltaY
                val eDistY = h2 / (2 * deltaY)
                ey = endY + eDistY
                ex = endX

                minimumArcDist2 = (midDist2 * mMinimumVerticalTangent
                        * mMinimumVerticalTangent)
            } else {
                // Same as above, but flip X & Y
                val eDistX = h2 / (2 * deltaX)
                ex = endX + eDistX
                ey = endY

                minimumArcDist2 = (midDist2 * mMinimumHorizontalTangent
                        * mMinimumHorizontalTangent)
            }
            val arcDistX = dx - ex
            val arcDistY = dy - ey
            val arcDist2 = arcDistX * arcDistX + arcDistY * arcDistY

            val maximumArcDist2 = midDist2 * mMaximumTangent * mMaximumTangent

            var newArcDistance2 = 0f
            if (arcDist2 < minimumArcDist2) {
                newArcDistance2 = minimumArcDist2
            } else if (arcDist2 > maximumArcDist2) {
                newArcDistance2 = maximumArcDist2
            }
            if (newArcDistance2 != 0f) {
                val ratio2 = newArcDistance2 / arcDist2
                val ratio = Math.sqrt(ratio2.toDouble()).toFloat()
                ex = dx + ratio * (ex - dx)
                ey = dy + ratio * (ey - dy)
            }
        }
        val controlX1 = (startX + ex) / 2
        val controlY1 = (startY + ey) / 2
        val controlX2 = (ex + endX) / 2
        val controlY2 = (ey + endY) / 2
        path.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY)
        return path
    }
}