package me.sweetll.tucao.business.personal.viewmodel

import android.app.AlertDialog
import android.databinding.ObservableField
import android.util.Log
import android.view.View
import com.jph.takephoto.model.TImage
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.business.home.event.RefreshPersonalEvent
import me.sweetll.tucao.business.personal.PersonalActivity
import me.sweetll.tucao.business.personal.fragment.PersonalFragment
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.toast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.jsoup.nodes.Document
import java.io.File

class PersonalViewModel(val activity: PersonalActivity, val fragment: PersonalFragment) : BaseViewModel() {
    val avatar = ObservableField<String>(user.avatar)
    val nickname = ObservableField<String>(user.name)
    val uuid = ObservableField<String>()
    val signature = ObservableField<String>(user.signature)

    fun refresh() {
        if (!user.isValid()) {
            activity.finish()
            return
        }
        avatar.set(user.avatar)
        nickname.set(user.name)
        signature.set(user.signature)
    }

    fun uploadAvatar(image: TImage) {
        val file = File(image.compressPath)
        val reqBody = RequestBody.create(MediaType.parse("image"), file)
        val formData = MultipartBody.Part.createFormData("avatar", file.name, reqBody)
        newApiService.updateAvatar(formData)
            .apiResult()
            .subscribe({ url ->
                user.avatar = url
                user.save()
                EventBus.getDefault().post(RefreshPersonalEvent())
                "修改头像成功".toast()
            }, { error ->
                Log.e("PersonalViewModel", "err = $error")
            })

    }

    fun onClickAvatar(view: View) {
        fragment.choosePickType()
    }

    fun onClickNickname(view: View) {
        activity.transitionToChangeInformation()
    }

    fun onClickSignature(view: View) {
        activity.transitionToChangeInformation()
    }

    fun onClickChangePassword(view: View) {
        activity.transitionToChangePassword()
    }

    fun onClickLogout(view: View) {
        val builder = AlertDialog.Builder(activity)
            .setMessage("真的要退出吗QAQ")
            .setPositiveButton("真的") { dialog, _ ->
                newApiService.logout()
                    .apiResult()
                    .subscribe({
                        Log.i("PersonalViewModel", "logout success")
                    }, {
                        Log.e("PersonalViewModel", "logout success")
                    })
                user.invalidate()
                EventBus.getDefault().post(RefreshPersonalEvent())
                dialog.dismiss()
                activity.finish()
            }
            .setNegativeButton("假的") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}
