package example.test.phong.core.data.search

import androidx.annotation.StringDef
import example.test.phong.core.data.api.model.Shot
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * fake API for searching dribbble
 */
interface DribbbleSearchService {
    @Deprecated("Move to DribbbleSearchService#searchDeferred")
    @GET("search")
    fun search(
            @Query("q") query: String,
            @Query("page") page: Int?,
            @Query("per_page") pageSize: Int,
            @Query("s") @SortOrder sort: String
              ): Call<List<Shot>>

    @GET("search")
    fun searchDeferred(
            @Query("q") query: String,
            @Query("page") page: Int?,
            @Query("s") sort: String,
            @Query("per_page") pageSize: Int
                      ): Deferred<Response<List<Shot>>>

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @StringDef(
            SORT_POPULAR,
            SORT_RECENT
              )
    annotation class SortOrder

    companion object {
        const val ENDPOINT = "https://dribbble.com/"
        const val SORT_POPULAR = ""
        const val SORT_RECENT = "latest"
        const val PER_PAGE_DEFAULT = 12
    }
}