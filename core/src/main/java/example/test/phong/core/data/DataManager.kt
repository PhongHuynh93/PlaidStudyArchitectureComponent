package example.test.phong.core.data

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import example.test.phong.core.data.prefs.SourceManager
import example.test.phong.core.designernews.domain.LoadStoriesUseCase
import example.test.phong.core.designernews.domain.SearchStoriesUseCase
import example.test.phong.core.producthunt.data.api.ProductHuntRepository
import example.test.phong.core.ui.FilterAdapter
import retrofit2.Call
import javax.inject.Inject

class DataManager @Inject constructor(
        private val filterAdapter: FilterAdapter,
        private val shotsRepository: ShotsReposity,
        private val loadStoriesUseCase: LoadStoriesUseCase,
        private val searchStoriesUseCase: SearchStoriesUseCase,
        private val productHuntRepository: ProductHuntRepository) : BaseDataManager<List<out PlaidItem>>(), LoadSourceCallback, LifecycleObserver {
    private lateinit var pageIndexes: MutableMap<String, Int>
    private var inflightCalls: MutableMap<String, Call<*>> = HashMap()
    val remoteData by lazy {
        MutableLiveData<List<PlaidItem>>()
    }

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

    override fun sourceLoaded(data: List<PlaidItem>?, page: Int, source: String) {
        loadFinished()
        if (data?.isNotEmpty() == true && sourceIsEnabled(source)) {
            setPage(data, page)
            setDataSource(data, source)
            onDataLoaded(data)
        }
        inflightCalls.remove(source)
    }

    override fun onDataLoaded(data: List<PlaidItem>) {
        remoteData.postValue(data)
    }

    private fun sourceIsEnabled(key: String): Boolean {
        return pageIndexes[key] != 0
    }

    override fun loadFailed(source: String) {
    }

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

    private fun loadDribbbleSearch(source: DribbbleSearchSource, page: Int) {
        shotsRepository.search(source.query, page, onResult = { result ->
            if (result is Result.Success) {
                sourceLoaded(result.data, page, source.key)
            } else {
                loadFailed(source.key)
            }
        })
    }

    private fun loadProductHunt(page: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun loadDesignerNewsStories(page: Int) {
        loadStoriesUseCase.invoke(page, this)
    }

    private fun getNextPageIndex(key: String): Int {
        var nextPage = 1
        if (pageIndexes.containsKey(key)) {
            nextPage = pageIndexes[key]!! + 1
        }
        pageIndexes[key] = nextPage
        return nextPage
    }
}