package me.sweetll.tucao.di.service

import io.reactivex.Observable
import me.sweetll.tucao.model.json.BaseResponse
import me.sweetll.tucao.model.json.NewBaseResp
import me.sweetll.tucao.model.json.RecommendResult
import me.sweetll.tucao.model.json.Video
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface NewApiService {

    @GET(ApiConfig.RECOMMEND)
    fun index(): Observable<NewBaseResp<RecommendResult>>

    @GET(ApiConfig.VIDEO_DETAIL)
    fun videoDetail(@Path("id") id: Int): Observable<NewBaseResp<Video>>

    @GET(ApiConfig.USER_VIDEOS)
    fun userVideos(@Path("id") uid: Int, @Query("page") page: Int): Observable<NewBaseResp<List<Video>>>

}