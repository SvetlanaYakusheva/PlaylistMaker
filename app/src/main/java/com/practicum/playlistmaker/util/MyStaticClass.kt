package com.practicum.playlistmaker.util

import android.content.Context
import android.util.TypedValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    if (s == null)  return "00:00"
    return SimpleDateFormat(
        "m:ss",
        Locale.getDefault()
    ).format(s)
}

fun <T> debounce(delayMillis: Long,
                 coroutineScope: CoroutineScope,
                 useLastParam: Boolean,
                 action: (T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (useLastParam) {
            debounceJob?.cancel()
        }
        if (debounceJob?.isCompleted != false || useLastParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(param)
            }
        }
    }
}