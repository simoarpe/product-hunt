package com.sarpex.producthunt.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sarpex.producthunt.data.AppContainer
import com.sarpex.producthunt.ui.MainDestinations.POST_ID_KEY
import com.sarpex.producthunt.ui.MainDestinations.USER_ID_KEY
import com.sarpex.producthunt.ui.home.HomeScreen
import com.sarpex.producthunt.ui.home.HomeScreenViewModel
import com.sarpex.producthunt.ui.post.PostDetailsScreen
import com.sarpex.producthunt.ui.user.UserScreen
import com.sarpex.producthunt.ui.user.UserScreenViewModel

/**
 * Destinations used in the ([ProductHuntApp]).
 */
object MainDestinations {
    const val HOME_ROUTE = "home"
    const val USER_ROUTE = "user"
    const val USER_ID_KEY = "userId"
    const val POST_ROUTE = "post"
    const val POST_ID_KEY = "postId"
}

@Composable
fun ProductHuntNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.HOME_ROUTE,
    homeScreenViewModel: HomeScreenViewModel,
    userScreenViewModel: UserScreenViewModel
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.HOME_ROUTE) {
            HomeScreen(
                navigateToPost = actions.navigateToPostDetails,
                homeScreenViewModel = homeScreenViewModel
            )
        }
        composable("${MainDestinations.USER_ROUTE}/{$USER_ID_KEY}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(USER_ID_KEY)
                ?: throw IllegalStateException("User id cannot be null.")
            UserScreen(
                userId = userId,
                userRepository = appContainer.userRepository,
                onBack = actions.upPress,
                navigateToPost = actions.navigateToPostDetails,
                userScreenViewModel = userScreenViewModel
            )
        }
        composable("${MainDestinations.POST_ROUTE}/{$POST_ID_KEY}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString(POST_ID_KEY)
                ?: throw IllegalStateException("Post id cannot be null.")
            PostDetailsScreen(
                postId = postId,
                onBack = actions.upPress,
                postRepository = appContainer.postRepository,
                navigateToUser = actions.navigateToUser
            )
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToPostDetails: (String) -> Unit = { postId: String ->
        navController.navigate("${MainDestinations.POST_ROUTE}/$postId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
    val navigateToUser: (String) -> Unit = { userId: String ->
        navController.navigate("${MainDestinations.USER_ROUTE}/$userId}")
    }
}
