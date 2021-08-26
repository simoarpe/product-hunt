package com.sarpex.producthunt.util

import com.sarpex.producthunt.model.PostAuthor
import com.sarpex.producthunt.model.PostItem
import com.sarpex.producthunt.model.PostsQuery
import com.sarpex.producthunt.model.Thumbnail
import com.sarpex.producthunt.model.UserQuery

fun PostsQuery.Edge.toPostItem(): PostItem {
    val author = PostAuthor(this.node.user.name, this.node.user.id)
    var thumbnail: Thumbnail? = null
    this.node.thumbnail?.let { queryThumbnail ->
        thumbnail = Thumbnail(queryThumbnail.type, queryThumbnail.url)
    }
    return (
        PostItem(
            node.id,
            node.name,
            node.tagline,
            node.votesCount.toString(),
            thumbnail,
            author
        )
        )
}

fun UserQuery.Edge.toPostItem(): PostItem {
    val author = PostAuthor(this.node.name, this.node.id)
    var thumbnail: Thumbnail? = null
    this.node.thumbnail?.let { queryThumbnail ->
        thumbnail = Thumbnail(queryThumbnail.type, queryThumbnail.url)
    }
    return (
        PostItem(
            node.id,
            node.name,
            node.tagline,
            node.votesCount.toString(),
            thumbnail,
            author
        )
        )
}
