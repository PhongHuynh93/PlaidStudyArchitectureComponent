package example.test.phong.core.data

import example.test.phong.core.data.api.model.Shot
import example.test.phong.core.dribbble.data.search.SearchRemoteDataSource
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class ShotsReposity @Inject constructor(private val contextProvider: CoroutinesContextProvider, private val
remoteDataSource: SearchRemoteDataSource) {
    private val inflight = mutableMapOf<String, Job>()
    private val shotCache = mutableMapOf<Long, Shot>()

    fun cancelAllSearches() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun search(query: String, page: Int, onResult: (Result<List<Shot>>) -> Unit) {
        val id = "$query::$page"
        inflight[id] = launchSearch(query, page, id, onResult)
    }

    private fun launchSearch(query: String, page: Int, id: String, onResult: (Result<List<Shot>>) -> Unit): Job = launch(contextProvider.io) {
        val result = remoteDataSource.search(query, page)
        inflight.remove(id)
        if (result is Result.Success) {
            cache(result.data)
        }
        withContext(contextProvider.main) {
            onResult(result)
        }
    }

    private fun cache(shots: List<Shot>) {
        shots.associateTo(shotCache) {
            it.id to it
        }
    }
}
