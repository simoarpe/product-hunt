package com.sarpex.producthunt.data.post

import android.content.Context
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.sarpex.producthunt.apolloClient
import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.model.PostQuery
import com.sarpex.producthunt.model.PostsQuery
import com.sarpex.producthunt.ui.theme.Image1
import com.sarpex.producthunt.util.toPixels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GraphQlPostRepository(applicationContext: Context) : PostRepository {
    private val apollo = apolloClient(applicationContext)

    override suspend fun getPost(postId: String): Result<PostQuery.Post> {
        return withContext(Dispatchers.IO) {
            val response: Response<PostQuery.Data> = try {
                apollo.query(PostQuery(postId, Image1.toPixels())).await()
            } catch (e: ApolloException) {
                // Handle protocol errors.
                return@withContext Result.Error(e)
            }

            val post = response.data?.post
            if (post == null || response.hasErrors()) {
                // Handle application errors.
                Result.Error(IllegalStateException())
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPosts(
        cursor: String,
        paging: Int,
        imageSize: Int
    ): Result<PostsQuery.Posts> {
        return withContext(Dispatchers.IO) {
            val response: Response<PostsQuery.Data> = try {
                apollo.query(PostsQuery(cursor, paging, imageSize)).await()
            } catch (e: ApolloException) {
                // Handle protocol errors.
                return@withContext Result.Error(e)
            }

            val posts = response.data?.posts
            if (posts == null || response.hasErrors()) {
                // Handle application errors.
                Result.Error(IllegalStateException())
            } else {
                Result.Success(posts)
            }
        }
    }
}
