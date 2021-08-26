package com.sarpex.producthunt.util

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp

fun Dp.toPixels(): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.value,
    Resources.getSystem().displayMetrics
).toInt()
