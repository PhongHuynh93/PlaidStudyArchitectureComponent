package example.test.phong.core.ui

import android.app.Activity
import android.app.ActivityOptions
import android.app.SharedElementCallback
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.util.Pair
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.util.ViewPreloadSizeProvider
import example.test.phong.core.R
import example.test.phong.core.data.DataLoadingCallbacks
import example.test.phong.core.data.DataLoadingSubject
import example.test.phong.core.data.PlaidItem
import example.test.phong.core.data.api.model.Shot
import example.test.phong.core.data.pocket.PocketUtils
import example.test.phong.core.data.stories.model.Story
import example.test.phong.core.producthunt.ui.ProductHuntPostHolder
import example.test.phong.core.ui.stories.StoryViewHolder
import example.test.phong.core.ui.widget.BadgedFourThreeImageView
import example.test.phong.core.util.Activities
import example.test.phong.core.util.intentTo

class FeedAdapter(val host: Activity, val dataLoading: DataLoadingSubject, val columns: Int,
                  val pocketIsInstalled: Boolean, val shotPreloadSizeProvider: ViewPreloadSizeProvider<Shot>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        DataLoadingCallbacks,
        ListPreloader.PreloadModelProvider<Shot> {

    companion object {
        fun createSharedElementReenterCallback(context: Context): SharedElementCallback {
            val shotTransitionName = context.getString(R.string.transition_shot)
            val shotBackgroundTransitionName = context.getString(R.string.transition_shot_background)

            return object : SharedElementCallback() {
                override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                    super.onMapSharedElements(names, sharedElements)
                }
            }
        }

        private val TYPE_DESIGNER_NEWS_STORY = 0
        private val TYPE_DRIBBBLE_SHOT = 1
        private val TYPE_PRODUCT_HUNT_POST = 2
        private val TYPE_LOADING_MORE = -1
        private val REQUEST_CODE_VIEW_SHOT: Int = 5407
    }

    private var layoutInflater: LayoutInflater = LayoutInflater.from(host)
    private val initialGifBadgeColor: Int
    private val shotLoadingPlaceholders: Array<ColorDrawable?>
    private var showLoadingMore: Boolean = false
    private var items: List<PlaidItem>

    init {
        // get the dribbble shot placeholder colors & badge color from the theme
        val a = host.obtainStyledAttributes(R.styleable.DribbbleFeed)
        val loadingColorArrayId = a.getResourceId(R.styleable.DribbbleFeed_shotLoadingPlaceholderColors, 0)
        if (loadingColorArrayId != 0) {
            val placeholderColors = host.resources.getIntArray(loadingColorArrayId)
            shotLoadingPlaceholders = arrayOfNulls<ColorDrawable>(placeholderColors.size)
            for (i in placeholderColors.indices) {
                shotLoadingPlaceholders[i] = ColorDrawable(placeholderColors[i])
            }
        } else {
            shotLoadingPlaceholders = arrayOf(ColorDrawable(Color.DKGRAY))
        }
        val initialGifBadgeColorId = a.getResourceId(R.styleable.DribbbleFeed_initialBadgeColor, 0)
        initialGifBadgeColor = if (initialGifBadgeColorId != 0)
            ContextCompat.getColor(host, initialGifBadgeColorId)
        else
            0x40ffffff

        items = ArrayList()

        dataLoading.registerCallback(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("register callback from datamanager")

        return when (viewType) {
            TYPE_DESIGNER_NEWS_STORY -> {
                createDesignerNewsStoryHolder(parent)
            }
            TYPE_DRIBBBLE_SHOT -> {
                createDribbbleShotHolder(parent)
            }
            TYPE_PRODUCT_HUNT_POST -> {
                createProductHuntStoryHolder(parent)
            }
            TYPE_LOADING_MORE -> {
                LoadingMoreHelper(layoutInflater.inflate(R.layout.infinite_loading, parent, false))
            }
            else -> throw IllegalStateException()
        }
    }

    private fun createProductHuntStoryHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ProductHuntPostHolder(layoutInflater.inflate(R.layout.product_hunt_item, parent, false),
                                     {
                                         openTabForProductHunt(it.discussionUrl)
                                     },
                                     {
                                         openTabForProductHunt(it.redirectUrl)
                                     })
    }

    private fun openTabForProductHunt(url: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun createDribbbleShotHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val holder = DribbbleShotHolder(layoutInflater.inflate(R.layout.dribbble_shot_item, parent, false))
        holder.image.setBadgeColor(initialGifBadgeColor)
        holder.image.setOnClickListener { view ->
            val options = ActivityOptions.makeSceneTransitionAnimation(
                    host,
                    Pair.create(view, host.getString(R.string.transition_shot)),
                    Pair.create(view, host.getString(R.string.transition_shot_background)))
            host.startActivityForResult(intentTo(Activities.Dribbble.Shot), REQUEST_CODE_VIEW_SHOT, options.toBundle())
        }
        holder.image.setOnTouchListener { v, event ->
            // start gif when touch
            val action = event.action
            if (!(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
                return@setOnTouchListener false
            }
            val drawable = holder.image.drawable
            drawable?.let {
                var gif: GifDrawable? = null
                if (it is TransitionDrawable) {
                    for (i in 0 until it.numberOfLayers) {
                        if (it.getDrawable(i) is GifDrawable) {
                            gif = it.getDrawable(i) as GifDrawable
                            break
                        }
                    }
                } else if (it is GifDrawable) gif = it

                gif?.let {
                    when (action) {
                        MotionEvent.ACTION_DOWN -> {
                            it.start()
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            it.stop()
                        }
                    }
                }
            }
            return@setOnTouchListener false
        }
        return holder
    }

    private fun createDesignerNewsStoryHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return StoryViewHolder(
                layoutInflater.inflate(R.layout.designer_news_story_items, parent, false),
                pocketIsInstalled,
                { story, adapterPosition ->
                    PocketUtils.addToPocket(host, url = story.url)
                    notifyItemChanged(adapterPosition, HomeGridItemAnimator.ADD_TO_POCKET)
                },
                { data ->
                    openDesignerNewstory(data)
                },
                { data ->
                    if (data.story.url != null) {
                        openTabDesignerNews(data.story)
                    } else {
                        openDesignerNewsStory(data)
                    }
                }
                              )
    }

    override fun getItemCount(): Int {
        return getDataItemCount() + if (showLoadingMore) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_DESIGNER_NEWS_STORY -> {
                if (holder is StoryViewHolder) {
                    holder.bind(getItem(position) as Story)
                }
            }
        }
    }


    private fun getItem(position: Int): PlaidItem? {
        if (position < 0 || (position >= items.size)) return null
        return items[position]
    }

    fun getItemColumnSpan(position: Int): Int {
        return when (getItemViewType(position)) {
            TYPE_LOADING_MORE -> columns
            else -> getItem(position)?.colspan ?: 0
        }
    }

    fun getDataItemCount(): Int {
        return items.size
    }

    fun getItemPosition(sharedShotId: Long): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun openDesignerNewsStory(data: StoryViewHolder.TransitionData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun openTabDesignerNews(story: Story) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun openDesignerNewstory(data: StoryViewHolder.TransitionData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPreloadItems(position: Int): MutableList<Shot> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPreloadRequestBuilder(item: Shot): RequestBuilder<*>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dataStartedLoading() {
        if (showLoadingMore) return
        showLoadingMore = true
        notifyItemChanged(getLoadingMoreItemPosition())
    }

    private fun getLoadingMoreItemPosition(): Int {
        return if (showLoadingMore) {
            itemCount - 1
        } else {
            RecyclerView.NO_POSITION
        }
    }

    override fun dataFinishedLoading() {
        if (!showLoadingMore) return
        showLoadingMore = false
        notifyItemRemoved(getLoadingMoreItemPosition())
    }

}

class DribbbleShotHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var image: BadgedFourThreeImageView = itemView as BadgedFourThreeImageView

}


class LoadingMoreHelper(itemview: View) : RecyclerView.ViewHolder(itemview) {
}
