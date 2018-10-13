package example.test.phong.core.dribbble.data.api

import example.test.phong.core.data.PlaidItemSorting
import example.test.phong.core.data.api.model.Shot

class ShotWeigher: PlaidItemSorting.PlaidItemGroupWeigher<Shot> {
    override fun weigh(shots: List<Shot>) {
        val maxLikes = (shots.maxBy {
            it.likesCount
        }?.likesCount?.toFloat() ?: 0f) + 1f
        shots.forEach {
            shot ->
            val weight = 1f - (shot.likesCount.toFloat() / maxLikes)
            shot.weight = shot.page + weight
        }
    }
}