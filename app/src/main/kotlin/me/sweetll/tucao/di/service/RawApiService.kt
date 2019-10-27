package me.sweetll.tucao.di.service

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RawApiService {
    @GET(ApiConfig.DANMU_API_URL)
    fun danmu(@Query("playerID") playerId: String,
              @Query("r") r: Long) : Observable<ResponseBody>

    @FormUrlEncoded
    @POST(ApiConfig.SEND_COMMENT_URL)
    fun sendComment(@Query("commentid") commentId: String,
                    @Field("content") content: String): Observable<ResponseBody>

    @GET
    @Streaming
    fun download(@Url url: String): Observable<Response<ResponseBody>>




    @GET(ApiConfig.SUPPORT_URL)
    fun support(@Query("commentid") commentId: String,
                @Query("id") id: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST(ApiConfig.SEND_REPLY_URL)
    fun sendReply(@Query("commentid") commentId: String,
                  @Query("id") id: String,
                  @Field("content") content: String): Observable<ResponseBody>


    @GET(ApiConfig.READ_MESSAGE_LIST_URL)
    fun readMessageList(): Observable<ResponseBody>

    @GET(ApiConfig.READ_MESSAGE_DETAIL_URL)
    fun readMessageDetail(@Query("messageid") messageId: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST(ApiConfig.REPLY_MESSAGE_URL)
    fun replyMessage(@Field("info[content]") content: String,
                     @Field("info[replyid]") replyId: String,
                     @Field("info[send_to_id]") senderId: String,
                     @Field("info[subject]") subject: String = "",
                     @Field("dosubmit") submit: String = "提交"): Observable<ResponseBody>

    @FormUrlEncoded
    @POST(ApiConfig.SEND_MESSAGE_URL)
    fun sendMessage(@Field("info[send_to_id]") sendToId: String,
                    @Field("info[subject]") subject: String,
                    @Field("info[content]") content: String,
                    @Field("dosubmit") submit: String = "发送"): Observable<ResponseBody>

}
