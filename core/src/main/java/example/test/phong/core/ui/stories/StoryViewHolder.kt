package example.test.phong.core.ui.stories

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.util.Pair
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import example.test.phong.core.R
import example.test.phong.core.data.stories.model.Story
import example.test.phong.core.ui.recyclerview.Divided
import example.test.phong.core.ui.transition.GravityArcMotion
import example.test.phong.core.ui.widget.BaselineGridTextView
import example.test.phong.core.util.AnimUtils
import example.test.phong.core.util.ViewUtils

class StoryViewHolder(itemView: View,
                      pocketIsInstalled: Boolean,
                      private val onPocketClicked: (story: Story, adapterPosition: Int) -> Unit,
                      private val onCommentsClicked: (data: TransitionData) -> Unit,
                      private val onItemClicked: (data: TransitionData) -> Unit
                     ) : RecyclerView.ViewHolder(itemView), Divided {

    private val title: BaselineGridTextView = itemView.findViewById(R.id.story_title)
    private val comments: TextView = itemView.findViewById(R.id.story_comments)
    private val pocket: ImageButton = itemView.findViewById(R.id.pocket)
    private var story: Story? = null

    fun createAddToPocketAnimator(): Animator {
        (pocket.parent.parent as ViewGroup).clipChildren = false
        val initialLeft = pocket.left
        val initialTop = pocket.top
        val translatedLeft = (itemView.width - pocket.width) / 2
        val translatedTop = initialTop - (itemView.height - pocket.height) / 2
        val arc = GravityArcMotion()

        val titleMoveFadeOut = ObjectAnimator.ofPropertyValuesHolder(
                title,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -(itemView.height / 5).toFloat()),
                PropertyValuesHolder.ofFloat(View.ALPHA, 0.54f))

        val pocketMoveUp = ObjectAnimator.ofFloat(
                pocket,
                View.TRANSLATION_X,
                View.TRANSLATION_Y,
                arc.getPath(initialLeft.toFloat(), initialTop.toFloat(), translatedLeft.toFloat(), translatedTop.toFloat()))

        val pocketScaleUp = ObjectAnimator.ofPropertyValuesHolder(pocket,
                                                                  PropertyValuesHolder.ofFloat(View.SCALE_X, 3f),
                                                                  PropertyValuesHolder.ofFloat(View.SCALE_Y, 3f))
        val pocketFadeUp = ObjectAnimator.ofInt<ImageView>(pocket,
                                                           ViewUtils.IMAGE_ALPHA, 255)

        val up = AnimatorSet().apply {
            playTogether(titleMoveFadeOut, pocketMoveUp, pocketScaleUp, pocketFadeUp)
            duration = 300L
            interpolator = AnimUtils.getFastOutSlowInInterpolator(itemView.context)
        }

        // animate everything back into place
        val titleMoveFadeIn = ObjectAnimator.ofPropertyValuesHolder(title,
                                                                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f),
                                                                    PropertyValuesHolder.ofFloat(View.ALPHA, 1f))
        val pocketMoveDown = ObjectAnimator.ofFloat(pocket,
                                                    View.TRANSLATION_X, View.TRANSLATION_Y,
                                                    arc.getPath(translatedLeft.toFloat(), translatedTop.toFloat(), 0f, 0f))
        val pvhPocketScaleDown = ObjectAnimator.ofPropertyValuesHolder(pocket,
                                                                       PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                                                                       PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f))
        val pocketFadeDown = ObjectAnimator.ofInt<ImageView>(pocket,
                                                             ViewUtils.IMAGE_ALPHA, 178)

        val down = AnimatorSet().apply {
            playTogether(titleMoveFadeIn, pocketMoveDown, pvhPocketScaleDown, pocketFadeDown)
            startDelay = 500L
            duration = 300L
            interpolator = AnimUtils.getFastOutSlowInInterpolator(itemView.context)
        }

        return AnimatorSet().apply {
            playSequentially(up, down)

            doOnEnd { (pocket.parent.parent as ViewGroup).clipChildren = true }
            doOnCancel {
                title.apply {
                    alpha = 1f
                    translationY = 0f
                }

                pocket.apply {
                    translationX = 0f
                    translationY = 0f
                    scaleX = 1f
                    scaleY = 1f
                    imageAlpha = 178
                }
            }
        }
    }


    fun bind(story: Story?) {
        this.story = story
        this.story?.let {
            title.text = it.title
            title.alpha = 1f // interrupted add to pocket anim can mangle
            comments.text = it.commentCount.toString()
            itemView.transitionName = it.url
        }
    }

    fun createStoryCommentReturnAnimator(): Animator {
        val animator = AnimatorSet()
        animator.playTogether(ObjectAnimator.ofFloat(pocket, View.ALPHA, 0f, 1f),
                             ObjectAnimator.ofFloat(comments, View.ALPHA, 0f, 1f))
        animator.duration = 120L
        animator.interpolator = AnimUtils.getLinearOutSlowInInterpolator(itemView.context)
        animator.doOnCancel {
            pocket.alpha = 1f
            comments.alpha = 1f
        }
        return animator
    }

    data class TransitionData(
            val story: Story,
            val position: Int,
            val title: BaselineGridTextView,
            val sharedElements: Array<Pair<View, String>>,
            val itemView: View
                             ) {

    }
}

