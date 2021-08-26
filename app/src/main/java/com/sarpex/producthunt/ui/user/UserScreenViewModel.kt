package com.sarpex.producthunt.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sarpex.producthunt.data.user.UserPostSource
import com.sarpex.producthunt.data.user.UserRepository
import com.sarpex.producthunt.model.UserQuery
import kotlinx.coroutines.flow.Flow

/**
 * User screen view model.
 */
class UserScreenViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getUserPosts(userId: String): Flow<PagingData<UserQuery.Edge>> {
        return Pager(PagingConfig(pageSize = 40)) {
            UserPostSource(userRepository, userId)
        }.flow
    }
}

class UserScreenViewModelFactory(val postsRepository: UserRepository) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @param <T>        The type parameter for the ViewModel.
     * @return a newly created ViewModel </T>
     * */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(UserRepository::class.java)
            .newInstance(postsRepository)
    }
}
