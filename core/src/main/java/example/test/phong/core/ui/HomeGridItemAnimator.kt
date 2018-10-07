package example.test.phong.core.ui

import android.animation.Animator
import android.util.Pair
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.recyclerview.widget.RecyclerView
import example.test.phong.core.ui.recyclerview.SlideInItemAnimator
import example.test.phong.core.ui.stories.StoryViewHolder

class HomeGridItemAnimator(): SlideInItemAnimator() {
    companion object {
        // constant payload, for use with Adapter#notifyItemChanged
        val ADD_TO_POCKET: Int = 1
        val STORY_COMMENTS_RETURN: Int = 2
    }

    private var pendingAddToPocket: StoryViewHolder? = null
    private var pendingStoryCommentsReturn: StoryViewHolder? = null
    private var runningAddToPocket: Pair<StoryViewHolder, Animator>? = null
    private var runningStoryCommentsReturn: Pair<StoryViewHolder, Animator>? = null

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun obtainHolderInfo(): ItemHolderInfo {
        return HomeGridItemHolderInfo()
    }

    override fun recordPreLayoutInformation(state: RecyclerView.State, viewHolder: RecyclerView.ViewHolder, changeFlags: Int, payloads: MutableList<Any>): ItemHolderInfo {
        val info = super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
        if (info is HomeGridItemHolderInfo) {
            info.animateAddPocket = payloads.contains(ADD_TO_POCKET)
            info.returnFromComments = payloads.contains(STORY_COMMENTS_RETURN)
        }
        return info
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder, preInfo: ItemHolderInfo, postInfo: ItemHolderInfo): Boolean {
        var runPending = super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        if (preInfo is HomeGridItemHolderInfo) {
            if (preInfo.animateAddPocket) {
                pendingAddToPocket = newHolder as StoryViewHolder
                runPending = true
            }
            if (preInfo.returnFromComments) {
                pendingStoryCommentsReturn = newHolder as StoryViewHolder
                runPending = true
            }
        }
        return runPending
    }

    override fun runPendingAnimations() {
        super.runPendingAnimations()
        pendingAddToPocket?.let {
            animateAddToPocket(it)
            pendingAddToPocket = null
        }

        pendingStoryCommentsReturn?.let {
            animateStoryCommentReturn(it)
            pendingStoryCommentsReturn = null
        }
    }

    private fun animateStoryCommentReturn(holder: StoryViewHolder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun animateAddToPocket(holder: StoryViewHolder) {
        endAnimation(holder)
        holder.createAddToPocketAnimator().apply {
            doOnStart {
                dispatchChangeStarting(holder, false)
            }

            doOnEnd {
                runningAddToPocket = null
                dispatchChangeFinished(holder, false)
            }

            doOnCancel {
                runningAddToPocket = null
                dispatchChangeFinished(holder, false)
            }
            runningAddToPocket = Pair.create(holder, this)
            start()
        }
    }

    override fun endAnimation(holder: RecyclerView.ViewHolder) {
        super.endAnimation(holder)
        if (holder === pendingAddToPocket) {
            dispatchChangeFinished(pendingAddToPocket, false)
            pendingAddToPocket = null
        }
        if (holder === pendingStoryCommentsReturn) {
            dispatchChangeFinished(pendingStoryCommentsReturn, false)
            pendingStoryCommentsReturn = null
        }
        if (runningAddToPocket != null && runningAddToPocket!!.first === holder) {
            runningAddToPocket!!.second.cancel()
        }
        if (runningStoryCommentsReturn != null && runningStoryCommentsReturn!!.first === holder) {
            runningStoryCommentsReturn!!.second.cancel()
        }
    }

    override fun endAnimations() {
        super.endAnimations()
        if (pendingAddToPocket != null) {
            dispatchChangeFinished(pendingAddToPocket, false)
            pendingAddToPocket = null
        }
        if (pendingStoryCommentsReturn != null) {
            dispatchChangeFinished(pendingStoryCommentsReturn, false)
            pendingStoryCommentsReturn = null
        }
        if (runningAddToPocket != null) {
            runningAddToPocket!!.second.cancel()
        }
        if (runningStoryCommentsReturn != null) {
            runningStoryCommentsReturn!!.second.cancel()
        }
    }

    override fun isRunning(): Boolean {
        return super.isRunning()
                || (runningAddToPocket != null && runningAddToPocket!!.second.isRunning)
                || (runningStoryCommentsReturn != null && runningStoryCommentsReturn!!.second.isRunning)
    }

    private class HomeGridItemHolderInfo() : RecyclerView.ItemAnimator.ItemHolderInfo() {
        var animateAddPocket: Boolean = false
        var returnFromComments: Boolean = false
    }
}