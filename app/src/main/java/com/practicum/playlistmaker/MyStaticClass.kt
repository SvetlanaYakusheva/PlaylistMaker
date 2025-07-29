package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
        ).toInt()
}

fun getCoverArtwork(imageCoverUrl: String?) : String {
    if (imageCoverUrl == null) return ""
    return imageCoverUrl.replaceAfterLast('/', "512x512bb.jpg")
}
