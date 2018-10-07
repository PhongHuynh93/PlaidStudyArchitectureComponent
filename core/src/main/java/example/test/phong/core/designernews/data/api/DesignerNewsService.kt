package example.test.phong.core.designernews.data.api

import example.test.phong.core.data.api.EnvelopePayload
import example.test.phong.core.designernews.stories.model.StoryResponse
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Models the Designer News API.
 *
 * v1 docs: https://github.com/layervault/dn_api
 * v2 docs: https://github.com/DesignerNews/dn_api_v2
 */
interface DesignerNewsService {
    @EnvelopePayload("stories")
    @GET("api/v2/stories")
    fun getStories(@Query("page") page: Int?): Deferred<Response<List<StoryResponse>>>

    companion object ENDPOINT {
        const val ENDPOINT = "https://www.designernews.co/"
    }
}