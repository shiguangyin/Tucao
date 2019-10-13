package me.sweetll.tucao.business.login.viewmodel

import android.databinding.ObservableField
import android.os.Handler
import android.os.Message
import android.util.Base64
import android.util.Patterns
import android.view.View
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.business.home.event.RefreshPersonalEvent
import me.sweetll.tucao.business.login.RegisterActivity
import me.sweetll.tucao.di.service.ApiConfig
import me.sweetll.tucao.extension.NonNullObservableField
import me.sweetll.tucao.extension.Variable
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.toast
import me.sweetll.tucao.util.MD5Util
import org.greenrobot.eventbus.EventBus
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.ref.WeakReference

class RegisterViewModel(val activity: RegisterActivity): BaseViewModel() {

    val codeBytes = ObservableField<ByteArray>()

    val account = NonNullObservableField("")
    val nickname = NonNullObservableField("")
    val email = NonNullObservableField("")
    val newPassword = NonNullObservableField("")
    val renewPassword = NonNullObservableField("")
    val code = NonNullObservableField("")

    val accountEnabled = NonNullObservableField(true)
    val nicknameEnabled = NonNullObservableField(true)
    val emailEnabled = NonNullObservableField(true)
    val newPasswordEnabled = NonNullObservableField(true)
    val renewPasswordEnabled = NonNullObservableField(true)
    val codeEnabled = NonNullObservableField(true)

    val accountError = NonNullObservableField("")
    val nicknameError = NonNullObservableField("")
    val emailError = NonNullObservableField("")
    val newError = NonNullObservableField("")
    val renewError = NonNullObservableField("")
    val codeError = NonNullObservableField("")

    var hasError: Boolean = false

    val finishRequest = Variable(false) // 标记请求是否完成
    val finishDelay = Variable(false)   // 标记延时是否完成
    var success = false
    var failMsg = ""

    companion object {

        const val MESSAGE_TRANSITION = 1
        const val TRANSITION_DELAY = 1000L

        class TransitionHandler(vm: RegisterViewModel): Handler() {

            private val vmRef = WeakReference<RegisterViewModel>(vm)

            override fun handleMessage(msg: Message?) {
                if (msg?.what == MESSAGE_TRANSITION) {
                    vmRef.get()?.let {
                        it.finishDelay.value = true
                    }
                } else {
                    super.handleMessage(msg)
                }
            }
        }
    }

    private val handler = TransitionHandler(this)

    init {
        checkCode()
    }

    private fun checkCode() {
        newApiService.getCaptcha()
            .bindToLifecycle(activity)
            .apiResult()
            .retryWhen(ApiConfig.RetryWithDelay())
            .subscribe({data ->
                val imageString = data.split(",")[1]
                val imageData = Base64.decode(imageString, Base64.DEFAULT)
                this.codeBytes.set(imageData)
            }, {error ->
                error.message?.toast()
            })
    }

    fun checkAccount() {

    }

    fun checkNickname() {

    }

    fun checkEmail() {

    }

    fun onClickCode(view: View) {
        checkCode()
    }

    fun onClickCreate(view: View) {
        hasError = false
        accountError.set("")
        nicknameError.set("")
        emailError.set("")
        newError.set("")
        renewError.set("")
        codeError.set("")

        if (account.get().length < 2 || account.get().length > 20) {
            hasError = true
            accountError.set("帐号应在2-20位之间")
        }

        if (nickname.get().length < 2 || nickname.get().length > 20) {
            hasError = true
            nicknameError.set("昵称应在2-20位之间")
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()) {
            hasError = true
            emailError.set("这不是一个合法的邮箱")
        }

        if (newPassword.get() != renewPassword.get()) {
            hasError = true
            newError.set("两次输入的密码不一致")
            renewError.set("两次输入的密码不一致")
        }

        if (newPassword.get().length < 6 || newPassword.get().length > 20) {
            hasError = true
            newError.set("密码应在6-20位之间")
        }

        if (renewPassword.get().length < 6 || renewPassword.get().length > 20) {
            hasError = true
            renewError.set("密码应在6-20位之间")
        }

        if (code.get().isEmpty()) {
            hasError = true
            codeError.set("验证码不能为空")
        }

        if (hasError) return

        accountEnabled.set(false)
        nicknameEnabled.set(false)
        emailEnabled.set(false)
        newPasswordEnabled.set(false)
        renewPasswordEnabled.set(false)
        codeEnabled.set(false)
        activity.startRegister()

        Observables.combineLatest(finishRequest.stream, finishDelay.stream) {
            a, b -> a && b
        }.distinctUntilChanged()
                .subscribe {
                    if (success) {
                        registerSuccess()
                    } else {
                        registerFailed(failMsg)
                    }
                }

        handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TRANSITION), TRANSITION_DELAY)
        val pwd = MD5Util.crypt(newPassword.get())
        newApiService.register(account.get(), nickname.get(), email.get(), pwd, code.get())
                .bindToLifecycle(activity)
                .apiResult()
                .subscribe({result ->
                    user.email = result.email
                    user.name = result.name
                    user.avatar = ""
                    user.level = 1
                    user.signature = ""
                    EventBus.getDefault().post(RefreshPersonalEvent())

                    success = true
                    finishRequest.value = true
                }, {
                    error ->
                    error.printStackTrace()

                    success = false
                    failMsg = error.message ?: "注册失败"
                    finishRequest.value = false
                })

    }


    private fun registerSuccess() {
        activity.registerSuccess()
    }

    private fun registerFailed(msg: String) {
        accountEnabled.set(true)
        nicknameEnabled.set(true)
        emailEnabled.set(true)
        newPasswordEnabled.set(true)
        renewPasswordEnabled.set(true)
        codeEnabled.set(true)
        activity.registerFailed(msg)
    }


}
