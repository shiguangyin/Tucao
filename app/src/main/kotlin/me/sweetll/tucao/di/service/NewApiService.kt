package me.sweetll.tucao.di.service

import io.reactivex.Observable
import me.sweetll.tucao.business.video.model.Comment
import me.sweetll.tucao.model.json.*
import me.sweetll.tucao.model.other.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import retrofit2.http.Part
import java.util.*


interface NewApiService {

    @GET(ApiConfig.RECOMMEND)
    fun videoRecommend(@Query("page") page: Int): Observable<NewBaseResp<VideoList>>

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
              @Field("captcha") captcha: String): Observable<NewBaseResp<User>>

    @POST(ApiConfig.USER_REGISTER)
    @FormUrlEncoded
    fun register(@Field("username") username: String, @Field("nickname") nickname: String,
                 @Field("mail") mail: String, @Field("password") password: String,
                 @Field("captcha") captcha: String): Observable<NewBaseResp<User>>

    @GET(ApiConfig.USER_LOGOUT)
    fun logout(): Observable<NewBaseResp<String>>

    @POST(ApiConfig.USER_AVATAR)
    @Multipart
    fun updateAvatar(@Part body: MultipartBody.Part): Observable<NewBaseResp<String>>

    @PUT(ApiConfig.USER_ME)
    @FormUrlEncoded
    fun updateUserInfo(@Field("nickname") nickname: String, @Field("biography") biography: String): Observable<NewBaseResp<String>>

    @PUT(ApiConfig.USER_UPDATE_PWD)
    @FormUrlEncoded
    fun updatePassword(@Field("password") password: String, @Field("new_password") newPassword: String): Observable<NewBaseResp<String>>

    @POST(ApiConfig.User_RESET_PWD)
    @FormUrlEncoded
    fun resetPassword(@Field("mail") mail: String, @Field("captcha") captcha: String): Observable<NewBaseResp<String>>


    @GET(ApiConfig.VIDEO_LIST)
    fun getVideoList(@Query("category") cateId: Int, @Query("page") page: Int,
                     @Query("size") size: Int, @Query("order") order: String = "date",
                     @Query("with_banner") withBanner: Int = 0): Observable<NewBaseResp<VideoList>>


    @POST(ApiConfig.VIDEO_DANMAKU)
    @FormUrlEncoded
    fun postDanmaku(@Path("id") vid: Int, @Field("part") part: Int,
                    @Field("time") time: Float, @Field("text") text: String,
                    @Field("font_size") size: Long = 25, @Field("color") color: Int = 16777215): Observable<NewBaseResp<String>>

}