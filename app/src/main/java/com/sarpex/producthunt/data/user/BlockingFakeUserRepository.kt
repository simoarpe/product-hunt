package com.sarpex.producthunt.data.user

import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.model.UserQuery

/**
 * Implementation of UserRepository that returns a hardcoded list of
 * users synchronously.
 */
class BlockingFakeUserRepository : UserRepository {
    override suspend fun getUser(
        userId: String,
        cursor: String,
        paging: Int,
        imageSize: Int
    ): Result<UserQuery.User> {
        return Result.Success(
            UserQuery.User(
                id = "user001",
                name = "Simone",
                username = "sarpex",
                headline = "Headline",
                profileImage = null,
                votedPosts = UserQuery.VotedPosts(
                    edges = listOf(),
                    pageInfo = UserQuery.PageInfo(
                        hasNextPage = false,
                        hasPreviousPage = false,
                        endCursor = null,
                        startCursor = null
                    ),
                    totalCount = 1
                )
            )
        )
    }
}
