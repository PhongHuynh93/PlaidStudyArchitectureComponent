package example.test.phong.core.data.stories

import example.test.phong.core.data.Result
import example.test.phong.core.designernews.data.api.DesignerNewsService
import example.test.phong.core.designernews.stories.model.StoryResponse
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class StoriesRemoteDataSource @Inject constructor(private val service: DesignerNewsService) {
    suspend fun loadStories(page: Int): Result<List<StoryResponse>> {
        return try {
            val response = service.getStories(page).await()
            getResult(response = response, onError = {
                Result.Error(IOException("Error getting stories ${response.code()} ${response.message()}"))
            })
        } catch (e: Exception) {
            Result.Error(IOException("Error getting stories", e))
        }
    }


    private inline fun getResult(
            response: Response<List<StoryResponse>>,
            onError: () -> Result.Error
                                ): Result<List<StoryResponse>> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return Result.Success(body)
            }
        }
        return onError.invoke()
    }
}