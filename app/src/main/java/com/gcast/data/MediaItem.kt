package com.gcast.data

data class MediaItem(
    val title: String,
    val url: String,
    val type: Type,
    val duration: Long? = null,
    val thumbnailUrl: String? = null
) {
    enum class Type {
        VIDEO,
        AUDIO,
        IMAGE
    }
}