package example.test.phong.core.util

import android.content.Context
import android.os.Build
import android.util.IntProperty
import android.util.Property
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator

class AnimUtils {
    companion object {
        private lateinit var linearOutSlowIn: Interpolator
        private lateinit var fastOutSlowIn: Interpolator

        fun getLinearOutSlowInInterpolator(context: Context): Interpolator {
            if (linearOutSlowIn == null) {
                linearOutSlowIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in)
            }
            return linearOutSlowIn
        }

        fun getFastOutSlowInInterpolator(context: Context): Interpolator {
            if (fastOutSlowIn == null) {
                fastOutSlowIn = AnimationUtils.loadInterpolator(context,
                                                                android.R.interpolator.fast_out_slow_in)
            }
            return fastOutSlowIn
        }

        fun <T> createIntProperty(impl: IntProp<T>): Property<T, Int> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                object : IntProperty<T>(impl.name) {
                    override fun get(`object`: T): Int? {
                        return impl[`object`]
                    }

                    override fun setValue(`object`: T, value: Int) {
                        impl[`object`] = value
                    }
                }
            } else {
                object : Property<T, Int>(Int::class.java, impl.name) {
                    override fun get(`object`: T): Int? {
                        return impl[`object`]
                    }

                    override fun set(`object`: T, value: Int?) {
                        impl[`object`] = value!!
                    }
                }
            }
        }
    }


    abstract class IntProp<T>(val name: String) {
        abstract operator fun set(`object`: T, value: Int)
        abstract operator fun get(`object`: T): Int
    }

}
