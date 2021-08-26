package com.sarpex.producthunt.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.sarpex.producthunt.R
import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.data.post.BlockingFakePostRepository
import com.sarpex.producthunt.model.PostsQuery
import com.sarpex.producthunt.ui.component.FullScreenLoading
import com.sarpex.producthunt.ui.component.InsetAwareTopAppBar
import com.sarpex.producthunt.ui.component.LoadingContent
import com.sarpex.producthunt.ui.post.PostCard
import com.sarpex.producthunt.ui.post.PostListDivider
import com.sarpex.producthunt.ui.state.UiState
import com.sarpex.producthunt.ui.theme.ProductHuntTheme
import com.sarpex.producthunt.util.produceUiState
import com.sarpex.producthunt.util.supportWideScreen
import com.sarpex.producthunt.util.toPostItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

/**
 * Stateful HomeScreen which manages state using [produceUiState]
 */
@Composable
fun HomeScreen(
    navigateToPost: (String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    homeScreenViewModel: HomeScreenViewModel
) {
    val (postUiState, refreshPost, clearError) = produceUiState(homeScreenViewModel) {
        Result.Success(posts)
    }

    HomeScreen(
        posts = postUiState.value,
        onRefreshPosts = refreshPost,
        onErrorDismiss = clearError,
        navigateToPost = navigateToPost,
        scaffoldState = scaffoldState
    )
}

/**
 * Responsible for displaying the Home Screen of this application.
 *
 * Stateless composable is not coupled to any specific state management.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    posts: UiState<Flow<PagingData<PostsQuery.Edge>>>,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: () -> Unit,
    navigateToPost: (String) -> Unit,
    scaffoldState: ScaffoldState
) {
    if (posts.hasError) {
        val errorMessage = stringResource(id = R.string.load_error)
        val retryMessage = stringResource(id = R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefreshPosts)
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
            val title = stringResource(id = R.string.app_name)
            InsetAwareTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(R.drawable.thumb_up),
                            contentDescription = stringResource(R.string.app_name),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        LoadingContent(
            empty = posts.initialLoad,
            emptyContent = { FullScreenLoading() },
            loading = posts.loading,
            onRefresh = onRefreshPosts,
            content = {
                HomeScreenErrorAndContent(
                    posts = posts,
                    onRefresh = {
                        onRefreshPosts()
                    },
                    navigateToPost = navigateToPost,
                    modifier = modifier.supportWideScreen()
                )
            }
        )
    }
}

/**
 * Responsible for displaying any error conditions around [PostList].
 */
@Composable
private fun HomeScreenErrorAndContent(
    posts: UiState<Flow<PagingData<PostsQuery.Edge>>>,
    onRefresh: () -> Unit,
    navigateToPost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (posts.data != null) {
        PostList(posts.data, navigateToPost, modifier)
    } else if (!posts.hasError) {
        // if there are no posts, and no error, let the user refresh manually
        TextButton(onClick = onRefresh, modifier.fillMaxSize()) {
            Text(
                stringResource(id = R.string.home_tap_to_load_content),
                textAlign = TextAlign.Center
            )
        }
    } else {
        // there's currently an error showing, don't show any content
        Box(modifier.fillMaxSize()) { /* empty screen */ }
    }
}

/**
 * Display a list of posts.
 *
 * When a post is clicked on, [navigateToPost] will be called to navigate to the detail screen
 * for that post.
 *
 * @param postItems (state) the list to display
 * @param navigateToPost (event) request navigation to Article screen
 * @param modifier modifier for the root element
 */
@Composable
private fun PostList(
    postItems: Flow<PagingData<PostsQuery.Edge>>,
    navigateToPost: (postId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyPostItems: LazyPagingItems<PostsQuery.Edge> = postItems.collectAsLazyPagingItems()

    // https://issuetracker.google.com/issues/177245496
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()
    LazyColumn(
        modifier = modifier
            .scrollable(scrollState, Orientation.Vertical),
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false
        ),
        state = listState
    ) {
        items(lazyPostItems) { post ->
            post?.let {
                PostCard(it.toPostItem(), navigateToPost)
                PostListDivider()
            }
        }
    }
}

@Preview("Post card")
@Preview("Post card (dark mode)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Post card (big font)", fontScale = 1.5f)
@Preview("Post card (large screen)", device = Devices.PIXEL_C)
@Composable
fun PreviewPostList() {
    val posts = runBlocking {
        (BlockingFakePostRepository().getPosts("", 0, 0) as Result.Success).data
    }
    ProductHuntTheme {
        Surface {
            LazyColumn {
                items(posts.edges) { post ->
                    post.let {
                        PostCard(post.toPostItem(), { /* Unused. */ })
                        PostListDivider()
                    }
                }
            }
        }
    }
}
