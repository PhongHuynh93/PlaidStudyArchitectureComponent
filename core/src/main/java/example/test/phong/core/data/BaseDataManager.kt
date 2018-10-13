package example.test.phong.core.data

import java.util.concurrent.atomic.AtomicInteger

/**
 * base class for loading data; extending types are responsible for providing implementations of [onDataLoaded] to do
 * something with the data and [cancelLoading] to cancel any activity
 */
abstract class BaseDataManager<T> : DataLoadingSubject {
    private val loadingCount: AtomicInteger by lazy {
        AtomicInteger(0)
    }

    private val loadingCallbacks: MutableList<DataLoadingCallbacks> by lazy {
        ArrayList<DataLoadingCallbacks>()
    }

    abstract fun onDataLoaded(data: T)
    abstract fun cancelLoading()

    protected fun loadStarted() {
        // first time - get the datas
        if (loadingCount.getAndIncrement() == 0) {
            dispatchLoadingStartedCallbacks()
        }
    }

    protected fun loadFinished() {
        // first time - get the datas
        if (loadingCount.decrementAndGet() == 0) {
            dispatchLoadingFinishedCallbacks()
        }
    }

    private fun dispatchLoadingFinishedCallbacks() {
        loadingCallbacks.forEach {
            it.dataFinishedLoading()
        }
    }

    private fun dispatchLoadingStartedCallbacks() {
        loadingCallbacks.forEach {
            it.dataStartedLoading()
        }
    }

    override fun registerCallback(callbacks: DataLoadingCallbacks) {
        loadingCallbacks.add(callbacks)
    }

    override fun unregisterCallback(callbacks: DataLoadingCallbacks) {
        if (loadingCallbacks.contains(callbacks))
            loadingCallbacks.remove(callbacks)
    }

    protected fun setPage(items: List<PlaidItem>, page: Int) {
        for (item in items) {
            item.page = page
        }
    }

    protected fun setDataSource(items: List<PlaidItem>, dataSource: String) {
        for (item in items) {
            item.dataSource = dataSource
        }
    }

    override fun isDataLoading(): Boolean {
        return loadingCount.get() > 0
    }

}