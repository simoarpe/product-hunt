package com.sarpex.producthunt.util

import com.sarpex.producthunt.model.PostQuery
import com.sarpex.producthunt.model.Thumbnail

fun getUrlOrEmpty(thumbnail: Thumbnail?): String =
    if (thumbnail?.url != null && thumbnail.type == "image") thumbnail.url else ""

fun getUrlOrNull(medium: PostQuery.Medium?): String? =
    if (medium?.url != null && medium.type == "image") medium.url else null
