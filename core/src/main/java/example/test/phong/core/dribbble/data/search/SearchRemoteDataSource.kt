package example.test.phong.core.dribbble.data.search

import example.test.phong.core.data.Result
import example.test.phong.core.data.api.model.Shot
import example.test.phong.core.data.search.DribbbleSearchService
import example.test.phong.core.data.search.DribbbleSearchService.Companion.PER_PAGE_DEFAULT
import example.test.phong.core.util.safeApiCall
import java.io.IOException
import javax.inject.Inject

class SearchRemoteDataSource @Inject constructor(private val service: DribbbleSearchService) {
    suspend fun search(query: String, page: Int, sortOrder: SortOrder = SortOrder.RECENT, pageSize: Int = PER_PAGE_DEFAULT)
            = safeApiCall(
            call = { requestSearch(query, page, sortOrder, pageSize) },
            errorMessage = "Error getting Dribbble data"
                         )

    private suspend fun requestSearch(
            query: String,
            page: Int,
            sortOrder: SortOrder = SortOrder.RECENT,
            pageSize: Int = PER_PAGE_DEFAULT
                                     ): Result<List<Shot>> {
        val response = service.searchDeferred(query, page, sortOrder.sort, pageSize).await()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return Result.Success(body)
            }
        }
        return Result.Error(
                IOException("Error getting Dribbble data ${response.code()} ${response.message()}")
                           )
    }

    enum class SortOrder(val sort: String) {
        POPULAR(""),
        RECENT("latest")
    }
}