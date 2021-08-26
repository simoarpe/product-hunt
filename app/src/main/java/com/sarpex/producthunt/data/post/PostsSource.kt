package com.sarpex.producthunt.data.post

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sarpex.producthunt.data.successOr
import com.sarpex.producthunt.model.PostsQuery
import com.sarpex.producthunt.ui.theme.Image1
import com.sarpex.producthunt.util.toPixels

/**
 * Post source class responsible of paginating the requests.
 */
class PostsSource(private val postRepository: PostRepository) :
    PagingSource<String, PostsQuery.Edge>() {
    /**
     * Loading API for [PagingSource].
     */
    override suspend fun load(params: LoadParams<String>): LoadResult<String, PostsQuery.Edge> {
        return try {
            val cursor = params.key ?: ""
            val postsResponse =
                postRepository.getPosts(cursor, 40, Image1.toPixels()).successOr(null)
            if (postsResponse == null) {
                LoadResult.Error(IllegalArgumentException("Unable to find posts"))
            } else {
                LoadResult.Page(
                    data = postsResponse.edges,
                    prevKey = null, // Only paging forward.
                    nextKey = if (postsResponse.pageInfo.hasNextPage) {
                        postsResponse.pageInfo.endCursor
                    } else null,
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * Provide a [Key] used for the initial [load] for the next [PagingSource] due to invalidation
     * of this [PagingSource]. The [Key] is provided to [load] via [LoadParams.key].
     *
     * The [Key] returned by this method should cause [load] to load enough items to
     * fill the viewport around the last accessed position, allowing the next generation to
     * transparently animate in. The last accessed position can be retrieved via
     * [state.anchorPosition][PagingState.anchorPosition], which is typically
     * the top-most or bottom-most item in the viewport due to access being triggered by binding
     * items as they scroll into view.
     *
     * For example, if items are loaded based on integer position keys, you can return
     * [state.anchorPosition][PagingState.anchorPosition].
     *
     * Alternately, if items contain a key used to load, get the key from the item in the page at
     * index [state.anchorPosition][PagingState.anchorPosition].
     *
     * @param state [PagingState] of the currently fetched data, which includes the most recently
     * accessed position in the list via [PagingState.anchorPosition].
     *
     * @return [Key] passed to [load] after invalidation used for initial load of the next
     * generation. The [Key] returned by [getRefreshKey] should load pages centered around
     * user's current viewport. If the correct [Key] cannot be determined, `null` can be returned
     * to allow [load] decide what default key to use.
     */
    override fun getRefreshKey(state: PagingState<String, PostsQuery.Edge>): String? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey is always null as we are paging only forward.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.nextKey
        }
    }
}
