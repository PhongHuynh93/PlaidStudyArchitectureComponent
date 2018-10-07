package example.test.phong.core.data.api.model

import com.google.gson.annotations.SerializedName
import example.test.phong.core.data.PlaidItem
import java.util.*

data class Shot(
        @SerializedName("id") override val id: Long,
        @SerializedName("title") override val title: String,
        @SerializedName("description") val description: String,
        @SerializedName("images") val images: Images,
        @SerializedName("views_count") val viewsCount: Long = 0L,
        @SerializedName("likes_count") val likesCount: Long = 0L,
        @SerializedName("created_at") val createdAt: Date? = null,
        @SerializedName("html_url") val htmlUrl: String = "https://dribbble.com/shots/$id",
        @SerializedName("animated") val animated: Boolean = false,
        @SerializedName("user") val user: User
               ): PlaidItem(id, title, htmlUrl) {
    var hasFadedIn = false
}