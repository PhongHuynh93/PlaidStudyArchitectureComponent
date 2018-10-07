package example.test.phong.plaidstudy

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import dagger.android.support.DaggerAppCompatActivity
import example.test.phong.core.data.DataManager
import example.test.phong.core.data.Source
import example.test.phong.core.data.api.model.Shot
import example.test.phong.core.designernews.DesignerNewsPrefs
import example.test.phong.core.ui.FeedAdapter
import example.test.phong.core.ui.FilterAdapter
import example.test.phong.core.ui.FiltersChangedCallbacks
import example.test.phong.core.ui.HomeGridItemAnimator
import example.test.phong.core.ui.recyclerview.GridItemDividerDecoration
import example.test.phong.core.ui.recyclerview.InfiniteScrollListener
import example.test.phong.core.util.Activities
import example.test.phong.core.util.Activities.Dribbble.Shot.RESULT_EXTRA_SHOT_ID
import example.test.phong.core.util.ConnectivityObserver
import example.test.phong.core.util.intentTo
import example.test.phong.plaidstudy.ui.recyclerview.FilterTouchHelperCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.no_connection.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    private val RC_SEARCH: Int = 0
    private val RC_NEW_DESIGNER_NEWS_STORY: Int = 4
    private val RC_NEW_DESIGNER_NEWS_LOGIN: Int = 5

    @Inject
    public lateinit var designerNewsPref: DesignerNewsPrefs
    @Inject
    public lateinit var dataManager: DataManager
    @Inject
    public lateinit var filterAdapter: FilterAdapter
    @Inject
    public lateinit var mainAdapter: FeedAdapter
    @Inject
    public lateinit var connectivityManager: ConnectivityManager
    @Inject
    public lateinit var networkInfo: NetworkInfo
    @Inject
    public lateinit var viewPreloadSizeProvider: ViewPreloadSizeProvider<Shot>

    private var connected: Boolean = true

    private val columns: Int by lazy {
        resources.getInteger(R.integer.num_columns)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            animateToolbar()
        }
        setExitSharedElementCallback(FeedAdapter.createSharedElementReenterCallback(this))

        // init main rcv
        grid?.apply {
            adapter = mainAdapter
            val gridlayoutManager = GridLayoutManager(this@MainActivity, columns).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return mainAdapter.getItemColumnSpan(position)
                    }
                }
            }
            layoutManager = gridlayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && gridlayoutManager.findFirstVisibleItemPosition() == 0
                            && gridlayoutManager.findViewByPosition(0)?.top ?: -1 == grid.paddingTop
                            && toolbar.translationZ != 0f) {
                        toolbar.translationZ = 0f
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING && toolbar.translationZ != -1f) {
                        // grid scrolled, lower toolbar to allow content to pass in front
                        toolbar.translationZ = -1f
                    }
                }
            })
            addOnScrollListener(object : InfiniteScrollListener(gridlayoutManager, dataManager) {
                override fun onLoadMore() {
                    dataManager.loadAllDataSources()
                }
            })
            addOnScrollListener(RecyclerViewPreloader<Shot>(this@MainActivity, mainAdapter, viewPreloadSizeProvider, 4))
            setHasFixedSize(true)
            addItemDecoration(GridItemDividerDecoration(this@MainActivity, R.dimen.divider_height, R.color.divider))
            itemAnimator = HomeGridItemAnimator()
        }

        //        drawer.setOnApplyWindowInsetsListener { v, insets ->
        //
        //        }

        // init drawer rcv
        setupTaskDescription()
        filters?.apply {
            adapter = filterAdapter
            itemAnimator = FilterAdapter.FilterAnimator()
            filterAdapter.registerFilterChangedCallback(object: FiltersChangedCallbacks {
                override fun onFilterChanged(changedFilter: Source) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onFilterRemoved(removed: Source) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
            val itemTouchHelper = ItemTouchHelper(FilterTouchHelperCallback(filterAdapter, this@MainActivity))
            itemTouchHelper.attachToRecyclerView(this)
        }

        checkEmptyState()

        lifecycle.addObserver(ConnectivityObserver(connectivityManager, networkInfo, object : ConnectivityObserver.Callback {
            override fun onConnectivityStateChange(connected: Boolean) {
                if (!connected) {
                    empty.visibility = View.GONE
                    if (no_connection == null) {
                        stub_no_connection.inflate()
                    }
                    val avd = getDrawable(R.drawable.avd_no_connection) as AnimatedVectorDrawable
                    if (no_connection != null && avd != null) {
                        no_connection.setImageDrawable(avd)
                        avd.start()
                    }
                } else {
                    if (mainAdapter.getDataItemCount() != 0) return
                    // if we dont have any data yet
                    runOnUiThread {
                        TransitionManager.beginDelayedTransition(drawer)
                        no_connection?.visibility = View.GONE
                        empty.visibility = View.VISIBLE
                        dataManager.loadAllDataSources()
                    }
                }
            }
        }))
        lifecycle.addObserver(dataManager)
        dataManager.remoteData.observe(this, Observer {
            Timber.e("getting list data from BE $it")
        })
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        if (data == null || resultCode != Activity.RESULT_OK || !data.hasExtra(RESULT_EXTRA_SHOT_ID)) return
        val sharedShotId = data.getLongExtra(RESULT_EXTRA_SHOT_ID, -1L)

        // when reentering, if the shared element is no longer on screen (e.g. after an orientation change) then scroll it into view
        if (sharedShotId != -1L && mainAdapter.getDataItemCount() > 0 && grid.findViewHolderForItemId(sharedShotId) == null) {
            val position = mainAdapter.getItemPosition(sharedShotId)
            if (position == RecyclerView.NO_POSITION) return
            postponeEnterTransition()
            grid.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    grid.removeOnLayoutChangeListener(this)
                    startPostponedEnterTransition()
                }
            })
            grid.scrollToPosition(position)
            toolbar.translationZ = -1f
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val designerNewsLogin = menu.findItem(R.id.menu_designer_news_login)
        designerNewsLogin?.let {
            designerNewsLogin.setTitle(if (designerNewsPref.isLoggedIn()) {
                R.string.designer_news_log_out
            } else { R.string.designer_news_login})
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_filter -> {
                drawer.openDrawer(GravityCompat.END)
                return true
            }
            R.id.menu_search -> {
                val searchMenuView = toolbar.findViewById(R.id.menu_search) as View
                val options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView, getString(R.string.transition_search_back)).toBundle()
                startActivityForResult(intentTo(Activities.Search), RC_SEARCH, options)
                return true
            }
            R.id.menu_designer_news_login -> {
                if (!designerNewsPref.isLoggedIn()) {
                    startActivity(intentTo(Activities.DesignerNews.Login))
                } else {
                    designerNewsPref.logout(this)
                    Toast.makeText(applicationContext, R.string.designer_news_logged_out, Toast.LENGTH_SHORT).show()
                }
                return true

            }
            R.id.menu_about -> {
                startActivity(intentTo(Activities.About))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else
            super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_SEARCH -> {

            }
            RC_NEW_DESIGNER_NEWS_LOGIN -> {

            }
            RC_NEW_DESIGNER_NEWS_STORY -> {

            }
        }
    }

    protected fun fabClick() {

    }

    private fun registerPostStoryResultListener() {

    }

    private fun unregisterPostStoryResultListener() {

    }

    private fun revealPostingProgress() {

    }

    private fun ensurePostingProgressInflated() {

    }

    private fun checkEmptyState() {

    }

    private fun showPostingProgress() {

    }

    private fun setNoFiltersEmptyTextVisibility(visibility: Int) {

    }

    private fun setupTaskDescription() {

    }

    private fun animateToolbar() {

    }

    private fun showFab() {

    }

    private fun highlightNewSource(sources: Source) {

    }
}
