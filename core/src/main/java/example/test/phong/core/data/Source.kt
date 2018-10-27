package example.test.phong.core.data

import androidx.annotation.DrawableRes
import example.test.phong.core.R
import java.util.*

open class Source(val key: String, val sortOrder: Int, val name: String, @DrawableRes val iconRes: Int, var active: Boolean) {
    class SourceComparator : Comparator<Source> {
        override fun compare(o1: Source, o2: Source): Int {
            return o1.sortOrder - o2.sortOrder
        }
    }

    fun isSwipeDismissable(): Boolean {
        return false
    }

    companion object {

    }
}

open class DribbbleSource(key: String, sortOrder: Int, name: String, active: Boolean) : Source(key, sortOrder, name, R.drawable.ic_dribbble, active)
class DribbbleSearchSource(val query: String, active: Boolean)
    : DribbbleSource(query + DRIBBBLE_QUERY_PREFIX, SEARCH_SORT_ORDER, "\"" + query + "\"", active) {

    companion object {
        val DRIBBBLE_QUERY_PREFIX = "DRIBBBLE_QUERY_"
        val SEARCH_SORT_ORDER = 400
    }
}
open class DesignerNewsSource(key: String, sortOrder: Int, name: String, active: Boolean) : Source(key, sortOrder, name, R.drawable.ic_designer_news, active)
class DesignerNewsSearchSource(query: String, active: Boolean)
    : DesignerNewsSource(query + DESIGNER_NEWS_QUERY_PREFIX, SEARCH_SORT_ORDER, "\"" + query + "\"", active) {
    companion object {
        val DESIGNER_NEWS_QUERY_PREFIX = "DRIBBBLE_NEWS_QUERY_"
        val SEARCH_SORT_ORDER = 200
    }
}
