package example.test.phong.core.data

class PlaidItemSorting {
    class PlaidItemComparator : Comparator<PlaidItem> {
        override fun compare(o1: PlaidItem?, o2: PlaidItem?): Int {
            return java.lang.Float.compare(o1!!.weight, o2!!.weight)
        }
    }

    interface PlaidItemGroupWeigher<T : PlaidItem> {
        fun weigh(items: List<T>)
    }

    class NaturalOrderWeigher : PlaidItemGroupWeigher<PlaidItem> {
        override fun weigh(items: List<PlaidItem>) {
            val step = 1f / items.size.toFloat()
            for (i in items.indices) {
                val item = items[i]
                item.weight = item.page + i.toFloat() * step
            }
        }
    }
}