package me.sweetll.tucao.business.video.model

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comment(@Json(name = "user_avatar")val avatar: String,
                   val level: String = "LV233",
                   @Json(name = "user_nickname")val nickname: String,
                   @Json(name = "likes") var likes: Int,
                   @Json(name = "order")val lch: String,
                   @Json(name = "create_time")val time: Long,
                   @Json(name = "content")val info: String,
                   val id: Int,
                   @Json(name = "reply_count")val replyNum: Int,
                   var hasSend: Boolean = true,
                   var support: Boolean = false) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.readLong(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            1 == source.readInt(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(avatar)
        writeString(level)
        writeString(nickname)
        writeInt(likes)
        writeString(lch)
        writeLong(time)
        writeString(info)
        writeInt(id)
        writeInt(replyNum)
        writeInt((if (hasSend) 1 else 0))
        writeInt((if (support) 1 else 0))
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Comment> = object : Parcelable.Creator<Comment> {
            override fun createFromParcel(source: Parcel): Comment = Comment(source)
            override fun newArray(size: Int): Array<Comment?> = arrayOfNulls(size)
        }
    }
}
