package me.sweetll.tucao.di.service

import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

object ApiConfig {
    const val API_KEY = "25tids8f1ew1821ed"

    const val BASE_RAW_API_URL = "http://www.tucao.one/"
    const val BASE_JSON_API_URL = "http://www.tucao.one/api_v2/"
    const val BASE_XML_API_URL = "http://www.tucao.one/"

    /*
     * Json
     */
    const val LIST_API_URL = "list.php"
    const val SEARCH_API_URL = "search.php"
    const val VIEW_API_URL = "view.php"
    const val RANK_API_URL = "rank.php"
    const val REPLY_API_URL = "http://www.tucao.one/index.php?m=comment&c=index&a=ajax"

    const val UPDATE_API_URL = "http://45.63.54.11:12450/api/app-portal/version"

    /*
     * Drrr
     */
    const val CREATE_POST_API_URL = "http://45.63.54.11:13450/comment/createTime"
    const val POSTS_API_URL = "http://45.63.54.11:13450/comments"
    const val CREATE_REPLY_API_URL = "http://45.63.54.11:13450/reply/createTime/{commentId}"
    const val REPLIES_API_URL = "http://45.63.54.11:13450/replies/{commentId}"
    const val CREATE_VOTE_API_URL = "http://45.63.54.11:13450/vote/{commentId}"

    /*
     * XML
     */
    const val PLAY_URL_API_URL = "http://api.tucao.one/api/playurl"
    const val DANMU_API_URL = "http://www.tucao.one/index.php?m=danmakuCount&c=index&a=init"

    /*
     * Raw
     */
    const val LIST_URL = "list/{tid}/"
    const val BGM_URL = "bgm/{year}/{month}/"
    const val SEND_DANMU_URL = "index.php?m=danmakuCount&c=index&a=post"
    const val COMMENT_URL = "index.php?m=comment&c=index&a=init&hot=0&iframe=1"
    const val SEND_COMMENT_URL = "index.php?m=comment&c=index&a=post"
    const val READ_MESSAGE_LIST_URL = "index.php?m=message&c=index&a=inbox"
    const val READ_MESSAGE_DETAIL_URL = "index.php?m=message&c=index&a=read"
    const val REPLY_MESSAGE_URL = "index.php?m=message&c=index&a=reply"
    const val SEND_MESSAGE_URL = "index.php?m=message&c=index&a=send"

    const val SUPPORT_URL = "index.php?m=comment&c=index&a=support&format=json"
    const val SEND_REPLY_URL = "index.php?m=comment&c=index&a=post&replyuid=undefined"

    fun generatePlayerId(hid: String, part: Int) = "11-$hid-1-$part"

    class RetryWithDelay(val maxRetries: Int = 3, val delayMillis: Long = 2000L) : Function<Observable<in Throwable>, Observable<*>> {
        var retryCount = 0

        override fun apply(observable: Observable<in Throwable>): Observable<*> = observable
            .flatMap { throwable ->
                if (++retryCount < maxRetries) {
                    Observable.timer(delayMillis, TimeUnit.MILLISECONDS)
                } else {
                    Observable.error(throwable as Throwable)
                }
            }
    }


    const val RECOMMEND = "/api/v1/feed/recommend"
    const val VIDEO_DETAIL = "/api/v1/video/v{id}"
    const val VIDEO_DANMAKU = "/api/v1/video/v{id}/danmaku"
    const val VIDEO_COMMENT = "/api/v1/video/v{id}/comments"
    const val VIDEO_LIST = "/api/v1/video/list"

    const val USER_VIDEOS = "/api/v1/user/u{id}/videos"
    const val USER_CAPTCHA = "/api/v1/user/captcha"
    const val USER_LOGIN = "/api/v1/user/login"
    const val USER_LOGOUT = "/api/v1/user/logout"
    const val USER_REGISTER = "/api/v1/user/register"
    const val USER_AVATAR = "/api/v1/user/avatar"
    const val USER_ME = "/api/v1/user/me"
    const val USER_UPDATE_PWD = "/api/v1/user/password"
    const val User_RESET_PWD = "/api/v1/user/password/forget"


}
