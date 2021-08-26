package com.sarpex.producthunt.data.post

import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.model.PostQuery
import com.sarpex.producthunt.model.PostsQuery

/**
 * Interface to the Post data layer.
 */
interface PostRepository {

    /**
     * Get a specific post.
     */
    suspend fun getPost(postId: String): Result<PostQuery.Post>

    /**
     * Get a paginated list of posts.
     */
    suspend fun getPosts(cursor: String, paging: Int, imageSize: Int): Result<PostsQuery.Posts>
}
