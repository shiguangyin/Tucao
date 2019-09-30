package me.sweetll.tucao.model.raw

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Banner(@Json(name = "img_url")val imgUrl: String,
                  @Json(name = "link_url")val linkUrl: String = "",
                  @Json(name ="vid") val hid: String?,
                  val title: String? = null)
