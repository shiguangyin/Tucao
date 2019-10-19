package me.sweetll.tucao.business.home.viewmodel

import android.view.View
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.business.channel.ChannelDetailActivity
import me.sweetll.tucao.business.home.fragment.VideoListFragment
import me.sweetll.tucao.business.showtimes.ShowtimeActivity
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.toast
import me.sweetll.tucao.model.json.Category

class VideoListViewModel(val fragment: VideoListFragment, private val category: Int): BaseViewModel() {


    fun loadData() {
        fragment.setRefreshing(true)
        newApiService.getVideoList(category, 0, 10)
            .apiResult()
            .doAfterTerminate{fragment.setRefreshing(false)}
            .subscribe({videoList ->
                if (videoList.categories.size % 3 != 0) {
                    val emptyCount = 3 - (videoList.categories.size % 3)
                    val newCateList = videoList.categories.toMutableList()
                    for (i in 0 until emptyCount) {
                        newCateList.add(Category(-1, ""))
                    }
                    val newList = videoList.copy(categories = newCateList)
                    fragment.loadVideoList(newList)
                } else {
                    fragment.loadVideoList(videoList)
                }
            },{err ->
                err.printStackTrace()
                err.message?.toast()
                fragment.loadError()
            })
    }



    fun onClickChannel(view: View) {
        ChannelDetailActivity.intentTo(fragment.activity!!, (view.tag as String).toInt())
    }

    fun onClickShowtime(view: View) {
        ShowtimeActivity.intentTo(fragment.activity!!)
    }
}