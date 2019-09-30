package me.sweetll.tucao.model.json

import com.squareup.moshi.JsonClass
import me.sweetll.tucao.model.raw.Banner

@JsonClass(generateAdapter = true)
data class RecommendResult(
    val banners: List<Banner> = emptyList(),
    val channels: List<FeedChannel> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FeedChannel(
    val name: String = "",
    val videos: List<Video> = emptyList()
)

