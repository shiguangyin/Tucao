package me.sweetll.tucao.di.service

import io.reactivex.Observable
import me.sweetll.tucao.business.video.model.Comment
import me.sweetll.tucao.model.json.*
import me.sweetll.tucao.model.other.User
import okhttp3.ResponseBody
import retrofit2.http.*
import java.util.*


interface NewApiService {

    @GET(ApiConfig.RECOMMEND)
    fun index(): Observable<NewBaseResp<RecommendResult>>

    @GET(ApiConfig.VIDEO_DETAIL)
    fun videoDetail(@Path("id") id: Int): Observable<NewBaseResp<Video>>

    @GET(ApiConfig.USER_VIDEOS)
    fun userVideos(@Path("id") uid: Int, @Query("page") page: Int): Observable<NewBaseResp<List<Video>>>

    @GET(ApiConfig.VIDEO_DANMAKU)
    fun videoDanmaku(@Path("id") vid: Int, @Query("part") partIndex: Int): Observable<ResponseBody>

    @GET(ApiConfig.VIDEO_COMMENT)
    fun videoComments(@Path("id") vid: Int, @Query("page") page: Int): Observable<NewBaseResp<List<Comment>>>

    @GET(ApiConfig.USER_CAPTCHA)
    fun getCaptcha(): Observable<NewBaseResp<String>>

    @POST(ApiConfig.USER_LOGIN)
    @FormUrlEncoded
    fun login(@Field("username") username: String, @Field("password") password: String,
              @Field("captcha")captcha: String): Observable<NewBaseResp<User>>

}