package com.sarpex.producthunt.data.post

import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.model.PostQuery
import com.sarpex.producthunt.model.PostsQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts synchronously.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BlockingFakePostRepository : PostRepository {

    override suspend fun getPost(postId: String): Result<PostQuery.Post> {
        val user = PostQuery.User(id = "user001", name = "Simone")
        val post = PostQuery.Post(
            id = "001",
            name = "Awesome Post",
            thumbnail = null,
            tagline = "My tagline",
            description = "Description",
            votesCount = 100,
            user = user,
            media = listOf(),
            makers = listOf()
        )
        return Result.Success(post)
    }

    override suspend fun getPosts(
        cursor: String,
        paging: Int,
        imageSize: Int
    ): Result<PostsQuery.Posts> {
        val edges = mutableListOf<PostsQuery.Edge>()
        val user = PostsQuery.User(id = "user001", name = "Simone")
        val node1 = PostsQuery.Node(
            id = "001",
            name = "Awesome Post",
            tagline = "My tagline",
            votesCount = 100,
            thumbnail = null,
            user = user
        )
        val node2 = PostsQuery.Node(
            id = "002",
            name = "Second Post",
            tagline = "Another tagline",
            votesCount = 99,
            thumbnail = null,
            user = user
        )
        edges.add(PostsQuery.Edge(node = node1, cursor = ""))
        edges.add(PostsQuery.Edge(node = node2, cursor = ""))
        val pageInfo = PostsQuery.PageInfo(
            hasPreviousPage = false,
            hasNextPage = false,
            endCursor = null,
            startCursor = null
        )
        return Result.Success(PostsQuery.Posts(edges = edges, pageInfo = pageInfo, totalCount = 10))
    }
}
