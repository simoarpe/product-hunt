package com.sarpex.producthunt.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.sarpex.producthunt.ProductHuntApplication
import com.sarpex.producthunt.ui.home.HomeScreenViewModel
import com.sarpex.producthunt.ui.home.HomeScreenViewModelFactory
import com.sarpex.producthunt.ui.user.UserScreenViewModel
import com.sarpex.producthunt.ui.user.UserScreenViewModelFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appContainer = (application as ProductHuntApplication).container

        val homeScreenViewModelFactory = HomeScreenViewModelFactory(appContainer.postRepository)
        val homeScreenViewModel: HomeScreenViewModel by viewModels { homeScreenViewModelFactory }

        val userScreenViewModelFactory = UserScreenViewModelFactory(appContainer.userRepository)
        val userScreenViewModel: UserScreenViewModel by viewModels { userScreenViewModelFactory }

        setContent {
            // ProductHuntApp(appContainer, homeScreenViewModel, userScreenViewModel)
        }
    }
}
