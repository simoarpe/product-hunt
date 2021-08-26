package com.sarpex.producthunt.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.sarpex.producthunt.R
import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.data.user.UserRepository
import com.sarpex.producthunt.model.UserQuery
import com.sarpex.producthunt.ui.component.FullScreenLoading
import com.sarpex.producthunt.ui.component.InsetAwareTopAppBar
import com.sarpex.producthunt.ui.component.LoadingContent
import com.sarpex.producthunt.ui.post.PostCard
import com.sarpex.producthunt.ui.post.PostListDivider
import com.sarpex.producthunt.ui.state.UiState
import com.sarpex.producthunt.ui.theme.Image1
import com.sarpex.producthunt.util.produceUiState
import com.sarpex.producthunt.util.supportWideScreen
import com.sarpex.producthunt.util.toPixels
import com.sarpex.producthunt.util.toPostItem
import kotlinx.coroutines.flow.Flow

/**
 * Stateful User Screen manages state using [produceUiState].
 */
@Composable
fun UserScreen(
    userId: String,
    userRepository: UserRepository,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onBack: () -> Unit,
    navigateToPost: (String) -> Unit,
    userScreenViewModel: UserScreenViewModel
) {
    val userInfo = produceUiState(userRepository) {
        getUser(userId, "", 0, Image1.toPixels())
    }
    val (userPosts, refreshUser, clearError) = produceUiState(userScreenViewModel) {
        val userPost = getUserPosts(userId)
        Result.Success(userPost)
    }

    UserScreen(
        userInfo = userInfo.result.value,
        userPosts = userPosts.value,
        onRefreshUser = refreshUser,
        onErrorDismiss = clearError,
        navigateToPost,
        onBack,
        scaffoldState = scaffoldState,
    )
}

/**
 * Stateless User Screen displays user info and user posts.
 */
@Composable
fun UserScreen(
    userInfo: UiState<UserQuery.User>,
    userPosts: UiState<Flow<PagingData<UserQuery.Edge>>>,
    onRefreshUser: () -> Unit,
    onErrorDismiss: () -> Unit,
    navigateToPost: (String) -> Unit,
    onBack: () -> Unit,
    scaffoldState: ScaffoldState,
) {
    if (userPosts.hasError || userInfo.hasError) {
        val errorMessage = stringResource(id = R.string.load_error)
        val retryMessage = stringResource(id = R.string.retry)

        // If onRefreshUser or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefreshUser)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Show snackbar using a coroutine, when the coroutine is cancelled the snackbar will
        // automatically dismiss. This coroutine will cancel whenever posts.hasError is false
        // (thanks to the surrounding if statement) or if scaffoldState.snackbarHostState changes.
        LaunchedEffect(scaffoldState.snackbarHostState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = retryMessage
            )
            when (snackbarResult) {
                SnackbarResult.ActionPerformed -> onRefreshPostsState()
                SnackbarResult.Dismissed -> onErrorDismissState()
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            InsetAwareTopAppBar(
                title = { Text("User Info") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = userPosts.initialLoad || userInfo.initialLoad,
            emptyContent = { FullScreenLoading() },
            loading = userPosts.loading || userInfo.loading,
            onRefresh = onRefreshUser,
            content = {
                UserScreenErrorAndContent(
                    userInfo = userInfo,
                    userPosts = userPosts,
                    onRefresh = { onRefreshUser() },
                    navigateToPost = navigateToPost,
                    modifier = modifier.supportWideScreen()
                )
            }
        )
    }
}

@Composable
private fun UserScreenErrorAndContent(
    userInfo: UiState<UserQuery.User>,
    userPosts: UiState<Flow<PagingData<UserQuery.Edge>>>,
    onRefresh: () -> Unit,
    navigateToPost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (userPosts.data != null && !userPosts.hasError && userInfo.data != null && !userInfo.hasError) {
        UserContent(userInfo.data, userPosts.data, navigateToPost, modifier)
    } else {
        // if there are no posts, and no errors, let the user refresh manually.
        TextButton(onClick = onRefresh, modifier.fillMaxSize()) {
            Text("😭 Ops... Something went wrong. Tap to try again.", textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun UserContent(
    userInfo: UserQuery.User,
    userPosts: Flow<PagingData<UserQuery.Edge>>,
    navigateToPost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(18.dp)
    ) {
        Image(
            painter = rememberImagePainter(
                data = userInfo.profileImage,
                builder = {
                    crossfade(true)
                }
            ),
            contentDescription = null, // decorative
            modifier = modifier
                .size(Image1, Image1)
                .clip(CircleShape)
        )
        Text(
            text = userInfo.name,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = userInfo.username,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(18.dp))
        Divider(
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
        )
        UserPostList(userPosts, navigateToPost, modifier)
    }
}

@Composable
private fun UserPostList(
    postItems: Flow<PagingData<UserQuery.Edge>>,
    navigateToPost: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyPostItems: LazyPagingItems<UserQuery.Edge> = postItems.collectAsLazyPagingItems()
    LazyColumn(
        modifier = modifier,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false
        )
    ) {
        items(lazyPostItems) { post ->
            post?.let {
                PostCard(it.toPostItem(), navigateToPost)
                PostListDivider()
            }
        }
    }
}
