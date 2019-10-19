package me.sweetll.tucao.model.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.sweetll.tucao.model.raw.Banner

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String = ""
)


@JsonClass(generateAdapter = true)
data class VideoList(
    @Json(name = "banners") val banners: List<Banner> = emptyList(),
    @Json(name = "sub_categories") val categories: List<Category> = emptyList(),
    @Json(name = "videos") val videos: List<Video> = emptyList()
    )