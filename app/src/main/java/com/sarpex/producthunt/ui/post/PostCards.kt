package com.sarpex.producthunt.ui.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sarpex.producthunt.data.Result
import com.sarpex.producthunt.data.post.BlockingFakePostRepository
import com.sarpex.producthunt.model.PostItem
import com.sarpex.producthunt.ui.theme.Image1
import com.sarpex.producthunt.ui.theme.ProductHuntTheme
import com.sarpex.producthunt.util.getUrlOrEmpty
import com.sarpex.producthunt.util.toPostItem
import kotlinx.coroutines.runBlocking

@Composable
fun Hunter(
    postItem: PostItem,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = postItem.author.name,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun PostImage(postItem: PostItem, modifier: Modifier = Modifier) {
    Image(
        painter = rememberImagePainter(
            data = getUrlOrEmpty(postItem.thumbnail),
            builder = {
                crossfade(true)
            }
        ),
        contentDescription = null, // decorative
        modifier = modifier
            .size(Image1, Image1)
            .clip(MaterialTheme.shapes.small)
    )
}

@Composable
fun PostTagline(postItem: PostItem) {
    Text(postItem.tagline, style = MaterialTheme.typography.subtitle1)
}

/**
 * Full-width divider with padding for [PostList]
 */
@Composable
fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Composable
fun PostCard(postItem: PostItem, navigateToPost: (String) -> Unit) {
    Row(
        Modifier
            .clickable(onClick = { navigateToPost(postItem.id) })
    ) {
        PostImage(
            postItem = postItem,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
        Column(
            Modifier
                .weight(1f)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = postItem.title,
                    style = MaterialTheme.typography.overline
                )
            }
            PostTagline(postItem = postItem)
            Hunter(
                postItem = postItem,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = postItem.votesCount,
                style = MaterialTheme.typography.overline,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Preview("Post Card")
@Composable
fun PostPreview() {
    val post = runBlocking {
        (BlockingFakePostRepository().getPosts("", 0, 0) as Result.Success).data.edges.first()
    }
    ProductHuntTheme {
        Surface {
            PostCard(post.toPostItem(), { /* Unused. */ })
        }
    }
}
