package me.sweetll.tucao.business.login.viewmodel

import android.databinding.ObservableField
import android.util.Base64
import android.view.View
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.schedulers.Schedulers
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.business.login.ForgotPasswordActivity
import me.sweetll.tucao.di.service.ApiConfig
import me.sweetll.tucao.extension.NonNullObservableField
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.sanitizeHtml
import me.sweetll.tucao.extension.toast
import org.jsoup.nodes.Document

class ForgotPasswordViewModel(val activity: ForgotPasswordActivity) : BaseViewModel() {

    val email = NonNullObservableField("")
    val code = NonNullObservableField("")
    val codeBytes = ObservableField<ByteArray>()

    init {
        checkCode()
    }

    private fun checkCode() {
        newApiService.getCaptcha()
            .bindToLifecycle(activity)
            .apiResult()
            .retryWhen(ApiConfig.RetryWithDelay())
            .subscribe({ data ->
                val imageString = data.split(",")[1]
                val imageData = Base64.decode(imageString, Base64.DEFAULT)
                this.codeBytes.set(imageData)
            }, { error ->
                error.message?.toast()
            })
    }

    fun onClickCode(view: View) {
        checkCode()
    }

    fun onClickSubmit(view: View) {
        if (email.get().isNullOrEmpty()) {
            "邮箱不能为空".toast()
            return
        }

        if (code.get().isNullOrEmpty()) {
            "验证码不能为空".toast()
            return
        }

        newApiService.resetPassword(email.get(), code.get())
            .bindToLifecycle(activity)
            .apiResult()
            .subscribe({
                activity.resetSuccess()
            }, { error ->
                error.printStackTrace()
                activity.resetFailed(error.message ?: "重设密码失败")
            })
    }


}
