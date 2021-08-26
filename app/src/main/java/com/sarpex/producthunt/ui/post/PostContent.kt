package com.sarpex.producthunt.ui.post

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.data.post.BlockingFakePostRepository
import com.sarpex.producthunt.model.PostQuery
import com.sarpex.producthunt.ui.theme.DefaultSpacerSize
import com.sarpex.producthunt.ui.theme.ProductHuntTheme
import com.sarpex.producthunt.util.getUrlOrNull
import kotlinx.coroutines.runBlocking

@Composable
fun PostContent(
    postItem: PostQuery.Post,
    modifier: Modifier = Modifier,
    navigateToUser: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = DefaultSpacerSize)
            .fillMaxWidth()
    ) {
        item { PostHeaderImages(postItem) }
        item {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = postItem.tagline,
                    style = MaterialTheme.typography.subtitle1,
                    lineHeight = 20.sp
                )
            }
            Spacer(Modifier.height(DefaultSpacerSize))
        }
        postItem.description?.let { description ->
            item {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.body2,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(48.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { navigateToUser(postItem.user.id) }
                    )
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = AnnotatedString(postItem.user.name),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
        items(postItem.makers) { maker ->
            UserDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { navigateToUser(maker.id) }
                    )
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = AnnotatedString(maker.name),
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
        item {
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun UserDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Composable
private fun PostHeaderImages(postItem: PostQuery.Post) {
    Column {
        LazyRow {
            items(postItem.media) { image ->
                getUrlOrNull(image)?.let {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .size(280.dp, 240.dp)
                            .padding(start = 16.dp, bottom = 16.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(
                                data = it,
                                builder = {
                                    crossfade(true)
                                }
                            ),
                            contentDescription = null, // decorative
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(DefaultSpacerSize))
    }
}

@Preview("Post content")
@Preview("Post content (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewPostContent() {
    ProductHuntTheme {
        Surface {
            val postContent = runBlocking {
                (BlockingFakePostRepository().getPost("") as Result.Success).data
            }
            PostContent(postItem = postContent, navigateToUser = { /* Unused. */ })
        }
    }
}
