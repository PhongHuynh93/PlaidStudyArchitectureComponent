package example.test.phong.core.producthunt.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import example.test.phong.core.producthunt.data.api.model.Post
import kotlinx.android.synthetic.main.product_hunt_item.view.*

class ProductHuntPostHolder(itemView: View,
                            private val commentsClicked: (post: Post) -> Unit,
                            private val viewClicked: (post: Post) -> Unit): RecyclerView.ViewHolder(itemView) {

    private var post: Post? = null

    init {
        itemView.story_comments.setOnClickListener { post?.let { commentsClicked(it) } }
        itemView.setOnClickListener { post?.let { viewClicked(it) } }
    }


    fun bind(item: Post) {
        post = item
        itemView.hunt_title.text = item.name
        itemView.tagline.text = item.tagline
        itemView.story_comments.text = item.commentsCount.toString()
    }
}