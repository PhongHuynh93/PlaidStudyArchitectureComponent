package example.test.phong.core.designernews.domain

import example.test.phong.core.data.PlaidItemSorting
import example.test.phong.core.data.stories.model.Story

class StoryWeigher: PlaidItemSorting.PlaidItemGroupWeigher<Story> {
    override fun weigh(items: List<Story>) {
        var maxVotes = 0f
        var maxComments = 0f
        for (story in items) {
            maxVotes = Math.max(maxVotes, story.voteCount.toFloat())
            maxComments = Math.max(maxComments, story.commentCount.toFloat())
        }
        for (story in items) {
            val weight = 1f - (story.commentCount.toFloat() / maxComments + story.voteCount.toFloat() / maxVotes) / 2f
            story.weight = story.page + weight
        }
    }
}
