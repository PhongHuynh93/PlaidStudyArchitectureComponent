package example.test.phong.core.producthunt.data.api

import example.test.phong.core.data.api.EnvelopePayload
import example.test.phong.core.producthunt.data.api.model.Post
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductHuntService {
    companion object {
        val ENDPOINT = "https://api.producthunt.com/"
    }

    @EnvelopePayload("posts")
    @GET("v1/posts")
    fun getPosts(@Query("days_ago") page: Int): Call<List<Post>>
}

