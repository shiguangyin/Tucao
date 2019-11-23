package me.sweetll.tucao.di.service

import io.reactivex.Observable
import me.sweetll.tucao.business.video.model.Clicli
import me.sweetll.tucao.model.json.Video
import me.sweetll.tucao.business.video.model.ReplyResponse
import me.sweetll.tucao.model.json.BaseResponse
import me.sweetll.tucao.model.json.ListResponse
import me.sweetll.tucao.model.json.Version
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface JsonApiService {


    @GET(ApiConfig.SEARCH_API_URL)
    fun search(@Query("tid") tid: Int?,
               @Query("page") pageIndex: Int,
               @Query("pagesize") pageSize: Int,
               @Query("order") order: String?,
               @Query("q") keyword: String): Observable<ListResponse<Video>>


    @GET(ApiConfig.UPDATE_API_URL)
    fun update(@Query("appKey") appKey: String,
               @Query("appSecret") appSecret: String,
               @Query("versionCode") versionCode: Int): Observable<Version>

    @GET(ApiConfig.REPLY_API_URL)
    fun reply(@Query("commentid") commentId: String,
              @Query("replyid") replyId: String,
              @Query("page") page: Int,
              @Query("num") num: Int): Observable<ReplyResponse>



}
