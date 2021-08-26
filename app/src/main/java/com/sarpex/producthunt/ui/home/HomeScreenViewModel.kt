package com.sarpex.producthunt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sarpex.producthunt.data.post.PostRepository
import com.sarpex.producthunt.data.post.PostsSource
import com.sarpex.producthunt.model.PostsQuery
import kotlinx.coroutines.flow.Flow

/**
 * Home screen view model.
 */
class HomeScreenViewModel(private val postRepository: PostRepository) : ViewModel() {
    val posts: Flow<PagingData<PostsQuery.Edge>> = Pager(PagingConfig(pageSize = 40)) {
        PostsSource(postRepository)
    }.flow
}

class HomeScreenViewModelFactory(val postRepository: PostRepository) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     *
     *
     * @param modelClass a `Class` whose instance is requested
     * @param <T>        The type parameter for the ViewModel.
     * @return a newly created ViewModel </T>
     * */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PostRepository::class.java)
            .newInstance(postRepository)
    }
}
