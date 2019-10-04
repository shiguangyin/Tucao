package me.sweetll.tucao.model.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DanmakuItem(
    val time: Float,
    val type: Int,
    @Json(name = "font_size") val fontSize: Int,
    val color: Int,
    val ctime: Long,
    val text: String
)