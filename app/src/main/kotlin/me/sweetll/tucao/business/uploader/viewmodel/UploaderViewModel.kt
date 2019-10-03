package me.sweetll.tucao.business.uploader.viewmodel

import android.view.View
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import me.sweetll.tucao.Const
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.model.json.Video
import me.sweetll.tucao.business.uploader.UploaderActivity
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.sanitizeHtml
import me.sweetll.tucao.extension.toast
import org.jsoup.nodes.Document

class UploaderViewModel(val activity: UploaderActivity, val userId: String) : BaseViewModel() {
    var pageIndex = 0
    var pageSize = 20

    init {
        loadData()
    }

    fun loadData() {
        pageIndex = 0
        val uid = userId.toInt()
        newApiService.userVideos(uid, pageIndex)
            .bindToLifecycle(activity)
            .apiResult()
            .subscribe({ videos ->
                pageIndex++
                activity.loadData(videos)
            }, { error ->
                error.printStackTrace()
            })
    }

    fun loadMoreData() {
        val uid = userId.toInt()
        newApiService.userVideos(uid, pageIndex)
            .bindToLifecycle(activity)
            .apiResult()
            .subscribe({ videos ->
                if (videos.size < pageSize) {
                    activity.loadMoreData(videos, Const.LOAD_MORE_END)
                } else {
                    pageIndex++
                    activity.loadMoreData(videos, Const.LOAD_MORE_COMPLETE)
                }
            }, { error ->
                error.printStackTrace()
            })
    }


    fun onClickSendMessage(view: View) {
        "不发不发就不发σ`∀´)".toast()
    }

}
