package com.practicum.playlistmaker.presentation

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale


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

fun getDateFormat(s: Long?) : String {
    if (s == null) return "00:00"
    return SimpleDateFormat(
        "mm:ss",
        Locale.getDefault()
    ).format(s)
}