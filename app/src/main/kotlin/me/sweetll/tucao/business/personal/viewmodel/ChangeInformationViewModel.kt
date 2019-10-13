package me.sweetll.tucao.business.personal.viewmodel

import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.business.home.event.RefreshPersonalEvent
import me.sweetll.tucao.business.personal.fragment.ChangeInformationFragment
import me.sweetll.tucao.extension.NonNullObservableField
import me.sweetll.tucao.extension.apiResult
import org.greenrobot.eventbus.EventBus

class ChangeInformationViewModel(val fragment: ChangeInformationFragment): BaseViewModel() {
    val nickname = NonNullObservableField(user.name)
    val signature = NonNullObservableField(user.signature)

    fun save() {
        newApiService.updateUserInfo(nickname.get(), signature.get())
            .bindToLifecycle(fragment)
            .apiResult()
            .subscribe({
                user.name = nickname.get()
                user.signature = signature.get()
                EventBus.getDefault().post(RefreshPersonalEvent())
                fragment.saveSuccess()
            },{ error ->
                error.printStackTrace()
                fragment.saveFailed(error.message ?: "保存失败")
            })
    }
}
