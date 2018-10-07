package example.test.phong.core.data.stories.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoryLinks(
        @SerializedName("user") val user: String,
        @SerializedName("comments") val comments: List<Long>,
        @SerializedName("upvotes") val upvotes: List<String>,
        @SerializedName("downvotes") val downvotes: List<String>
                     ): Parcelable