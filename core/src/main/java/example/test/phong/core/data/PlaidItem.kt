package example.test.phong.core.data

abstract class PlaidItem(@Transient open val id: Long, @Transient open val title: String, @Transient open var url: String? = null) {
    var dataSource: String? = null
    var page: Int = 0
    var weight: Float = 0F
    var colspan: Int = 0
}
