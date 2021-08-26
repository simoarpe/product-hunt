package com.sarpex.producthunt

import android.content.Context
import com.sarpex.producthunt.data.AppContainer
import com.sarpex.producthunt.data.post.BlockingFakePostRepository
import com.sarpex.producthunt.data.post.PostRepository
import com.sarpex.producthunt.data.user.BlockingFakeUserRepository
import com.sarpex.producthunt.data.user.UserRepository

class TestAppContainer(private val context: Context) : AppContainer {

    override val postRepository: PostRepository by lazy {
        BlockingFakePostRepository()
    }
    override val userRepository: UserRepository by lazy {
        BlockingFakeUserRepository()
    }
}
