package example.test.phong.core.data.api.model

data class Images(val hidpi: String? = null,
                  val normal: String? = null,
                  val teaser: String? = null) {
    fun best(): String? {
        return if (!hidpi.isNullOrEmpty()) hidpi else normal
    }

    fun bestSize(): ImageSize {
        return if (!hidpi.isNullOrEmpty()) ImageSize.TWO_X_IMAGE_SIZE else ImageSize.NORMAL_IMAGE_SIZE
    }

    enum class ImageSize(val width: Int, val height: Int) {
        NORMAL_IMAGE_SIZE(400, 300),
        TWO_X_IMAGE_SIZE(800, 600);

        operator fun component1() = width
        operator fun component2() = height
    }
}