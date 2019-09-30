package me.sweetll.tucao.di.service

import io.reactivex.Observable
import me.sweetll.tucao.model.json.BaseResponse
import me.sweetll.tucao.model.json.NewBaseResp
import me.sweetll.tucao.model.json.RecommendResult
import okhttp3.ResponseBody
import retrofit2.http.GET


interface NewApiService {

    @GET(ApiConfig.RECOMMEND)
    fun index(): Observable<NewBaseResp<RecommendResult>>

}