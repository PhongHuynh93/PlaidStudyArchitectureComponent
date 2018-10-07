package example.test.phong.core.ui.recyclerview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import example.test.phong.core.util.AnimUtils

/**
 * a ItemAnimator that fades and slides newly added items in from a given direction
 */
open class SlideInItemAnimator(var slideFromEdge: Int = Gravity.BOTTOM, layoutDirection: Int = -1): DefaultItemAnimator() {
    private val pendingAdds: MutableList<RecyclerView.ViewHolder> = ArrayList()
    init {
        slideFromEdge = Gravity.getAbsoluteGravity(slideFromEdge, layoutDirection)
        addDuration = 160L
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        when (slideFromEdge) {
            Gravity.START -> {
                holder.itemView.translationX = -holder.itemView.width / 3f
            }
            Gravity.TOP -> {
                holder.itemView.translationY = -holder.itemView.height / 3f
            }
            Gravity.END -> {
                holder.itemView.translationY = -holder.itemView.width / 3f
            }
            else -> {
                // bottom
                holder.itemView.translationY = holder.itemView.height / 3f
            }
        }
        pendingAdds.add(holder)
        return true
    }

    override fun runPendingAnimations() {
        super.runPendingAnimations()
        if (pendingAdds.isNotEmpty()) {
            for (i in pendingAdds.size - 1 downTo 0) {
                val holder = pendingAdds[i]
                holder.itemView.animate()
                        .alpha(1f)
                        .translationX(0f)
                        .translationY(0f)
                        .setDuration(addDuration)
                        .setListener(object: AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                animation.listeners.remove(this)
                                dispatchAddFinished(holder)
                                dispatchFinishedWhenDone()
                            }

                            override fun onAnimationCancel(animation: Animator) {
                                clearAnimatedValues(holder.itemView)
                            }

                            override fun onAnimationStart(animation: Animator) {
                                dispatchAddStarting(holder)
                            }
                        }).interpolator = AnimUtils.getLinearOutSlowInInterpolator(holder.itemView.context)
                pendingAdds.removeAt(i)
            }
        }
    }

    override fun endAnimation(holder: RecyclerView.ViewHolder) {
        holder.itemView.animate().cancel()
        if (pendingAdds.remove(holder)) {
            dispatchAddFinished(holder)
            clearAnimatedValues(holder.itemView)
        }
        super.endAnimation(holder)
    }

    override fun endAnimations() {
        for (i in pendingAdds.indices.reversed()) {
            val holder = pendingAdds[i]
            clearAnimatedValues(holder.itemView)
            dispatchAddFinished(holder)
            pendingAdds.removeAt(i)
        }
        super.endAnimations()
    }

    override fun isRunning(): Boolean {
        return pendingAdds.isNotEmpty() || super.isRunning()
    }

    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    private fun clearAnimatedValues(view: View) {
        view.alpha = 1f
        view.translationX = 0f
        view.translationY = 0f
    }
}