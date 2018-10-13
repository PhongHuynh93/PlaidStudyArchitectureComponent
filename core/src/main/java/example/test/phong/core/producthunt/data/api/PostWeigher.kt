package example.test.phong.core.producthunt.data.api

import example.test.phong.core.data.PlaidItemSorting
import example.test.phong.core.producthunt.data.api.model.Post

class PostWeigher: PlaidItemSorting.PlaidItemGroupWeigher<Post> {
    override fun weigh(items: List<Post>) {
        var maxVotes = 0f
        var maxComments = 0f
        for (post in items) {
            maxVotes = Math.max(maxVotes, post.votesCount.toFloat())
            maxComments = Math.max(maxComments, post.commentsCount.toFloat())
        }
        for (post in items) {
            val weight = 1f - (post.commentsCount.toFloat() / maxComments + post.votesCount.toFloat() / maxVotes) / 2f
            post.weight = post.page + weight
        }
    }
}
