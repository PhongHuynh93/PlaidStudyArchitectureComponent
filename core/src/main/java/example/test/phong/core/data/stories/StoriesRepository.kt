package example.test.phong.core.data.stories

import example.test.phong.core.data.Result
import example.test.phong.core.designernews.stories.model.StoryResponse
import javax.inject.Inject

class StoriesRepository @Inject constructor(private val remoteDataSource: StoriesRemoteDataSource){
    private val cache = mutableMapOf<Long, StoryResponse>()

    suspend fun loadStories(page: Int) = getData {
        remoteDataSource.loadStories(page)
    }

    private suspend fun getData(request: suspend () -> Result<List<StoryResponse>>): Result<List<StoryResponse>> {
        val result = request()
        if (result is Result.Success) {
            cache(result.data)
        }
        return result
    }

    private fun cache(data: List<StoryResponse>) {
        data.associateTo(cache) {
            it.id to it
        }
    }
}