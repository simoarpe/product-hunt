package com.sarpex.producthunt.ui.post

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.sarpex.producthunt.R
import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.data.post.BlockingFakePostRepository
import com.sarpex.producthunt.data.post.PostRepository
import com.sarpex.producthunt.model.PostQuery
import com.sarpex.producthunt.ui.component.InsetAwareTopAppBar
import com.sarpex.producthunt.ui.theme.ProductHuntTheme
import com.sarpex.producthunt.util.produceUiState
import com.sarpex.producthunt.util.supportWideScreen
import kotlinx.coroutines.runBlocking

/**
 * Stateful Post Details Screen that manages state using [produceUiState]
 */
@Composable
fun PostDetailsScreen(
    postId: String,
    postRepository: PostRepository,
    onBack: () -> Unit,
    navigateToUser: (String) -> Unit
) {
    val (post) = produceUiState(postRepository, postId) {
        getPost(postId)
    }

    val postData = post.value.data ?: return

    PostDetailsScreen(
        postItem = postData,
        onBack = onBack,
        navigateToUser = navigateToUser
    )
}

/**
 * Stateless Post Screen that displays a single post.
 */
@Composable
fun PostDetailsScreen(
    postItem: PostQuery.Post,
    onBack: () -> Unit,
    navigateToUser: (String) -> Unit
) {

    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) {
        FunctionalityNotAvailablePopup { showDialog = false }
    }

    Scaffold(
        topBar = {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = postItem.name
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                postItem = postItem,
                onUnimplementedAction = { showDialog = true }
            )
        }
    ) { innerPadding ->
        PostContent(
            postItem = postItem,
            navigateToUser = navigateToUser,
            modifier = Modifier
                // innerPadding takes into account the top and bottom bar
                .padding(innerPadding)
                // offset content in landscape mode to account for the navigation bar
                .navigationBarsPadding(bottom = false)
                // center content in landscape mode
                .supportWideScreen()
        )
    }
}

/**
 * Bottom bar for Post screen.
 */
@Composable
private fun BottomBar(
    postItem: PostQuery.Post,
    onUnimplementedAction: () -> Unit
) {
    Surface(elevation = 8.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .navigationBarsPadding()
                .height(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = onUnimplementedAction) {
                Icon(
                    imageVector = Icons.Filled.ThumbUpOffAlt,
                    contentDescription = stringResource(R.string.cd_add_to_favorites)
                )
            }
            IconButton(onClick = onUnimplementedAction) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.cd_share)
                )
            }
            IconButton(onClick = onUnimplementedAction) {
                Icon(
                    painter = painterResource(R.drawable.ic_text_settings),
                    contentDescription = stringResource(R.string.cd_text_settings)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onUnimplementedAction) {
                Text(text = postItem.votesCount.toString())
            }
        }
    }
}

/**
 * Display a popup explaining functionality not available.
 *
 * @param onDismiss (event) request the popup be dismissed.
 */
@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = stringResource(id = R.string.article_functionality_not_available),
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}

@Preview("Post details screen")
@Preview("Post details screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Post details screen (big font)", fontScale = 1.5f)
@Preview("Post details screen (large screen)", device = Devices.PIXEL_C)
@Composable
fun PreviewPostDetailsScreen() {
    ProductHuntTheme {
        val postContent = runBlocking {
            (BlockingFakePostRepository().getPost("") as Result.Success).data
        }
        PostDetailsScreen(postContent, { /* Unused. */ }, { /* Unused. */ })
    }
}
