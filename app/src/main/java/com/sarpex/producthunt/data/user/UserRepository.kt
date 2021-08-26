package com.sarpex.producthunt.data.user

import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.model.UserQuery

/**
 * Interface to the User data layer.
 */
interface UserRepository {
    /**
     * Get a specific user with a list of paginated posts he voted for.
     */
    suspend fun getUser(
        userId: String,
        cursor: String,
        paging: Int,
        imageSize: Int
    ): Result<UserQuery.User>
}
