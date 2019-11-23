package me.sweetll.tucao.business.home.viewmodel

import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.business.home.fragment.RecommendFragment
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.toast

class RecommendViewModel(val fragment: RecommendFragment) : BaseViewModel() {

    fun loadData() {
        fragment.setRefreshing(true)
        newApiService.videoRecommend(0).bindToLifecycle(fragment)
            .apiResult()
            .doAfterTerminate { fragment.setRefreshing(false) }
            .subscribe({ result ->
                fragment.loadVideoList(result)
            }, { error ->
                error.printStackTrace()
                error.message?.toast()
                fragment.loadError()
            })

    }
}
