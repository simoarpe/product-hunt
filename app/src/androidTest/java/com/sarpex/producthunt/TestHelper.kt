package com.sarpex.producthunt

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.sarpex.producthunt.data.post.BlockingFakePostRepository
import com.sarpex.producthunt.data.user.BlockingFakeUserRepository
import com.sarpex.producthunt.ui.ProductHuntApp
import com.sarpex.producthunt.ui.home.HomeScreenViewModel
import com.sarpex.producthunt.ui.user.UserScreenViewModel

/**
 * Launches the app from a test context.
 */
fun ComposeContentTestRule.launchProductHuntApp(context: Context) {
    setContent {
        ProductHuntApp(
            TestAppContainer(context),
            HomeScreenViewModel(BlockingFakePostRepository()),
            UserScreenViewModel(BlockingFakeUserRepository())
        )
    }
}
