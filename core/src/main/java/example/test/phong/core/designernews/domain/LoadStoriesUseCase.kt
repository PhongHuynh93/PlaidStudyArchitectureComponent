package example.test.phong.core.designernews.domain

import example.test.phong.core.data.CoroutinesContextProvider
import example.test.phong.core.data.LoadSourceCallback
import example.test.phong.core.data.Result
import example.test.phong.core.data.prefs.SourceManager
import example.test.phong.core.data.stories.StoriesRepository
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class LoadStoriesUseCase @Inject constructor(private val storiesRepository: StoriesRepository, private val
contextProvider: CoroutinesContextProvider) {
    private val parentJobs = mutableMapOf<String, Job>()

    fun cancelAllSearches() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun invoke(page: Int, callback: LoadSourceCallback) {
        val jobId = "${SourceManager.SOURCE_DESIGNER_NEWS_POPULAR}::$page"
        parentJobs[jobId] = launchLoad(page, callback, jobId)
    }

    private fun launchLoad(page: Int, callback: LoadSourceCallback, jobId: String)
    = launch(contextProvider.io) {
        val result = storiesRepository.loadStories(page)
        parentJobs.remove(jobId)
        if (result is Result.Success) {
            val stories = result.data.map {
                it.toStory()
            }
            withContext(contextProvider.main) {
                callback.sourceLoaded(stories, page, SourceManager.SOURCE_DESIGNER_NEWS_POPULAR)
            }
        } else {
            withContext(contextProvider.main) {
                callback.loadFailed(SourceManager.SOURCE_DESIGNER_NEWS_POPULAR)
            }
        }
    }

}
