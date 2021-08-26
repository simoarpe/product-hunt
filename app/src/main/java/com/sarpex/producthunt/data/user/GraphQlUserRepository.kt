package com.sarpex.producthunt.data.user

import android.content.Context
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.sarpex.producthunt.apolloClient
import com.sarpex.producthunt.model.UserQuery
import com.sarpex.producthunt.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * User repository using Apollo server.
 */
class GraphQlUserRepository(applicationContext: Context) : UserRepository {
    private val apollo = apolloClient(applicationContext)

    override suspend fun getUser(userId: String, cursor: String, paging: Int, imageSize: Int): Result<UserQuery.User> {
        return withContext(Dispatchers.IO) {
            val response: Response<UserQuery.Data> = try {
                apollo.query(UserQuery(userId, imageSize, cursor, paging)).await()
            } catch (e: ApolloException) {
                // Handle protocol errors.
                return@withContext Result.Error(e)
            }

            val user = response.data?.user
            if (user == null || response.hasErrors()) {
                // Handle application errors.
                Result.Error(IllegalStateException())
            } else {
                Result.Success(user)
            }
        }
    }
}
