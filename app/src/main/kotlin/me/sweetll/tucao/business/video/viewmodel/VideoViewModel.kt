package me.sweetll.tucao.business.video.viewmodel

import android.databinding.ObservableField
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.sweetll.tucao.AppApplication
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.model.json.Part
import me.sweetll.tucao.model.json.Video
import me.sweetll.tucao.business.video.VideoActivity
import me.sweetll.tucao.extension.*
import me.sweetll.tucao.model.xml.Durl
import me.sweetll.tucao.rxdownload.entity.DownloadStatus
import java.io.File
import java.io.FileOutputStream

class VideoViewModel(val activity: VideoActivity) : BaseViewModel() {
    val video = ObservableField<Video>()

    var playUrlDisposable: Disposable? = null
    var danmuDisposable: Disposable? = null

    var selectedPart = 0


    constructor(activity: VideoActivity, video: Video) : this(activity) {
        this.video.set(video)
    }

    fun queryVideo(id: Int) {
        newApiService.videoDetail(id).bindToLifecycle(activity)
            .apiResult()
            .subscribe({ video ->
                this.video.set(video)
                activity.loadVideo(video)
            }, { error ->
                error.printStackTrace()
                activity.binding.player.loadText?.let {
                    it.text = it.text.replace("获取视频信息...".toRegex(), "获取视频信息...[失败]")
                }
            })
    }


    fun queryPlayUrls(videoId: Int, hid: String, part: Part) {
        if (playUrlDisposable != null && !playUrlDisposable!!.isDisposed) {
            playUrlDisposable!!.dispose()
        }
        if (danmuDisposable != null && !danmuDisposable!!.isDisposed) {
            danmuDisposable!!.dispose()
        }

        if (part.flag == DownloadStatus.COMPLETED) {
            activity.loadDurls(part.durls)
        } else if (part.file.isNotEmpty()) {
            if ("clicli" !in part.file) {
                // 这个视频是直传的
                activity.loadDurls(mutableListOf(Durl(url = part.file)))
            }
        } else {
            playUrlDisposable = xmlApiService.playUrl(part.type, part.vid, System.currentTimeMillis() / 1000)
                .bindToLifecycle(activity)
                .subscribeOn(Schedulers.io())
                .flatMap { response ->
                    if (response.durls.isNotEmpty()) {
                        Observable.just(response.durls)
                    } else {
                        Observable.error(Throwable("请求视频接口出错"))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ duals ->
                    activity.loadDurls(duals)
                }, { error ->
                    error.printStackTrace()
                    activity.binding.player.loadText?.let {
                        it.text = it.text.replace("解析视频地址...".toRegex(), "解析视频地址...[失败]")
                    }
                })
        }

        danmuDisposable = newApiService.videoDanmaku(videoId, part.order)
            .bindToLifecycle(activity)
            .subscribeOn(Schedulers.io())
            .map { responseBody ->
                val outputFile = File.createTempFile("temp_danmaku", ".json", AppApplication.get().cacheDir)
                val outputStream = FileOutputStream(outputFile)

                outputStream.write(responseBody.bytes())
                outputStream.flush()
                outputStream.close()
                outputFile.absolutePath
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ uri ->
                activity.loadDanmaku(uri)
            }, { error ->
                error.printStackTrace()
                activity.binding.player.loadText?.let {
                    it.text = it.text.replace("全舰弹幕装填...".toRegex(), "全舰弹幕装填...[失败]")
                }
            })
    }

    fun sendDanmaku(time: Float, message: String) {
        val v = video.get() ?: return
        newApiService.postDanmaku(v.id, selectedPart, time, message)
            .bindToLifecycle(activity)
            .apiResult()
            .subscribe({
                // success
                "success".logD("VideoViewModel")
            }, Throwable::printStackTrace)

    }
}
