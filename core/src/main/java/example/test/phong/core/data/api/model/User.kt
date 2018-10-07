package example.test.phong.core.data.api.model

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("username") val username: String,
        @SerializedName("avatar_url") val avatarUrl: String? = null
               ) {
    val highQualityAvatarUrl: String? by lazy {
        avatarUrl?.replace("/normal/", "/original/")
    }
}
