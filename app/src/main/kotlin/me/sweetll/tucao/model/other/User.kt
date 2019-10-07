package me.sweetll.tucao.model.other

import com.bumptech.glide.signature.ObjectKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import me.sweetll.tucao.extension.edit
import me.sweetll.tucao.extension.getSharedPreference

@JsonClass(generateAdapter = true)
class User {

    @Json(name="mail")
    var email: String = ""
    @Json(name="nickname")
    var name: String = ""
    @Json(name="avatar")
    var avatar: String = ""
    var level: Int = 0
    @Json(name="biography")
    var signature: String = ""
    var message: Int = 0

    fun isValid() = name.isNotEmpty()

    fun invalidate() {
        email = ""
        name = ""
        avatar = ""
        level = 0
        signature = ""
        message = 0
        save()
    }

    fun save() {
        val userJson = adapter.toJson(this)
        SP_USER.getSharedPreference().edit {
            putString(KEY_USER, userJson)
        }
    }


    fun update(user: User) {
        email = user.email
        name = user.name
        avatar = user.avatar
        level = user.level
        signature = user.signature
        message = user.message
        save()
    }

    companion object {
        private const val SP_USER = "user"
        private const val KEY_USER = "user"
        private const val KEY_SIGNATURE = "signature"

        private val adapter by lazy {
            val moshi = Moshi.Builder()
                    .build()
            moshi.adapter(User::class.java)
        }

        fun load(): User {
            try {
                val userJson = SP_USER.getSharedPreference().getString(KEY_USER ,"")
                val user = adapter.fromJson(userJson)
                return user!!
            } catch (e: Exception) {
                return User()
            }
        }

        fun updateSignature(): Long {
            val time = System.currentTimeMillis()
            SP_USER.getSharedPreference().edit {
                putLong(KEY_SIGNATURE, time)
            }
            return time
        }

        fun signature(): ObjectKey {
            var signature = SP_USER.getSharedPreference().getLong(KEY_SIGNATURE, 0)
            if (signature == 0L) {
                signature = updateSignature()
            }
            return ObjectKey(signature)
        }
    }
}
