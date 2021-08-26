package com.sarpex.producthunt.model

data class PostItem(
    val id: String,
    val title: String,
    val tagline: String,
    val votesCount: String,
    val thumbnail: Thumbnail? = null,
    val author: PostAuthor,
)

data class Thumbnail(
    // Can become an enum.
    val type: String,
    val url: String? = null
)

data class PostAuthor(
    val name: String,
    val id: String
)
