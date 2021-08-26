package com.sarpex.producthunt.data

import android.content.Context
import com.sarpex.producthunt.data.post.GraphQlPostRepository
import com.sarpex.producthunt.data.post.PostRepository
import com.sarpex.producthunt.data.user.GraphQlUserRepository
import com.sarpex.producthunt.data.user.UserRepository

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val postRepository: PostRepository
    val userRepository: UserRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val userRepository: UserRepository by lazy {
        GraphQlUserRepository(applicationContext)
    }

    override val postRepository: PostRepository by lazy {
        GraphQlPostRepository(applicationContext)
    }
}
