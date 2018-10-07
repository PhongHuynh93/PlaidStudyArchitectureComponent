package example.test.phong.core.data

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import example.test.phong.core.data.prefs.SourceManager
import example.test.phong.core.designernews.domain.LoadStoriesUseCase
import example.test.phong.core.designernews.domain.SearchStoriesUseCase
import example.test.phong.core.producthunt.data.api.ProductHuntRepository
import example.test.phong.core.ui.FilterAdapter
import retrofit2.Call
import javax.inject.Inject

class DataManager @Inject constructor(context: Context, val filterAdapter: FilterAdapter,
                  private val shotsRepository: ShotsReposity,
                  private val loadStoriesUseCase: LoadStoriesUseCase,
                  private val searchStoriesUseCase: SearchStoriesUseCase,
                  private val productHuntRepository: ProductHuntRepository): BaseDataManager<List<out PlaidItem>>(), LoadSourceCallback, LifecycleObserver {
    private lateinit var pageIndexes: MutableMap<String, Int>
    private var inflightCalls: MutableMap<String, Call<*>>  = HashMap()

    init {
        setupPageIndexes()
    }

    private fun setupPageIndexes() {
        val dataSources = filterAdapter.filters
        pageIndexes = HashMap(dataSources.size)
        for (source in dataSources) {
            pageIndexes[source.key] = 0
        }
    }

    override fun isDataLoading(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerCallback(callbacks: DataLoadingSubject.DataLoadingCallbacks) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterCallback(callbacks: DataLoadingSubject.DataLoadingCallbacks) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sourceLoaded(result: List<PlaidItem>?, page: Int, source: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadFailed(source: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun loadAllDataSources() {
        for (filter in filterAdapter.filters) {
            loadSource(filter)
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun cancelLoading() {
        if (inflightCalls.isNotEmpty()) {
            for (call in inflightCalls.values) {
                call.cancel()
            }
            inflightCalls.clear()
        }
        shotsRepository.cancelAllSearches()
        loadStoriesUseCase.cancelAllSearches()
        searchStoriesUseCase.cancelAllSearches()
        productHuntRepository.cancelAllSearches()
    }

    private fun loadSource(source: Source) {
        if (source.active) {
            loadStarted()
            val page = getNextPageIndex(source.key)
            when (source.key) {
                SourceManager.SOURCE_DESIGNER_NEWS_POPULAR -> {
                    loadDesignerNewsStories(page)
                }
                SourceManager.SOURCE_PRODUCT_HUNT -> {
                    loadProductHunt(page)
                }
                else -> {
                    if (source is DribbbleSearchSource) {
                        loadDribbbleSearch(source, page)
                    } else if (source is DesignerNewsSearchSource) {
                        loadDesignerNewsSearch(source, page)
                    }
                }
            }
        }
    }

    private fun loadDesignerNewsSearch(source: DesignerNewsSearchSource, page: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadDribbbleSearch(source: DribbbleSearchSource, page: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadProductHunt(page: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadDesignerNewsStories(page: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getNextPageIndex(key: String): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataLoaded(data: List<out PlaidItem>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}