package example.test.phong.core.producthunt.data.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import example.test.phong.core.data.PlaidItem
import kotlinx.android.parcel.Parcelize

@Parcelize
class Post(
        @SerializedName("id") override val id: Long,
        @SerializedName("title") override val title: String,
        @SerializedName("url") override var url: String? = null,
        @SerializedName("name") val name: String,
        @SerializedName("tagline") val tagline: String,
        @SerializedName("discussion_url") val discussionUrl: String,
        @SerializedName("redirect_url") val redirectUrl: String,
        @SerializedName("comments_count") val commentsCount: Int,
        @SerializedName("votes_count") val votesCount: Int
          ) : PlaidItem(id, title, url), Parcelable