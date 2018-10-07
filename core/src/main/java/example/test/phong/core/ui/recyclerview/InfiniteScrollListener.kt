package example.test.phong.core.ui.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import example.test.phong.core.data.DataLoadingSubject

abstract class InfiniteScrollListener(private val layoutManager: LinearLayoutManager,
                                     private val dataLoading: DataLoadingSubject): RecyclerView.OnScrollListener() {
    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val loadMoreRunnable = Runnable { onLoadMore() }
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy < 0 || dataLoading.isDataLoading()) return
        val visibleItemCount = recyclerView.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

        if (totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD) {
            recyclerView.post(loadMoreRunnable)
        }
    }

    abstract fun onLoadMore()
}