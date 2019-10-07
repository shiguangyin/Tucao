package me.sweetll.tucao.business.login.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.databinding.ObservableField
import android.support.design.widget.Snackbar
import android.util.Base64
import android.util.Log
import android.view.View
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.sweetll.tucao.base.BaseViewModel
import me.sweetll.tucao.business.login.ForgotPasswordActivity
import me.sweetll.tucao.business.login.LoginActivity
import me.sweetll.tucao.business.login.RegisterActivity
import me.sweetll.tucao.di.service.ApiConfig
import me.sweetll.tucao.extension.NonNullObservableField
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.toast
import me.sweetll.tucao.util.MD5Util
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class LoginViewModel(val activity: LoginActivity): BaseViewModel() {

    val email = NonNullObservableField("")
    val password = NonNullObservableField("")
    val code = NonNullObservableField("")
    val codeBytes = ObservableField<ByteArray>()

    val container = NonNullObservableField(View.VISIBLE)
    val progress = NonNullObservableField(View.GONE)

    init {
        getCode()
    }


    private fun getCode() {
//        rawApiService.checkCode()
//                .bindToLifecycle(activity)
//                .subscribeOn(Schedulers.io())
//                .retryWhen(ApiConfig.RetryWithDelay())
//                .subscribe({
//                    body ->
//                    this.codeBytes.set(body.bytes())
//                }, {
//                    error ->
//                    error.printStackTrace()
//                    error.message?.toast()
//                })
        newApiService.getCaptcha()
            .bindToLifecycle(activity)
            .apiResult()
            .retryWhen(ApiConfig.RetryWithDelay())
            .subscribe({data ->
                //data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPAAAABQCAMAAAAQlwhOAAAAP1BMVEUAAAB0Z2ItIBtzZmE4KyZVSENeUUx5bGcSBQBXSkVENzKIe3YcDwp1aGOBdG+ajYiGeXRaTUiMf3pHOjVXSkVy3seJAAAAAXRSTlMAQObYZgAABERJREFUeJzsWut2syAQZE9O86NNqjbv/649uQB7BSQQjWa+H1+V244jy4BxH3zwwQc5HI/HpUN4KY7HXTEedkZ4GIZ9EXbD/ubwWjEtHcCLMU27Y7x0ABHgYOkQWiJHBhwAbIjxg8zf359Sdme7MYk9X4Ux3LSFO5aIrR9UwoAV3hhjhTB4gd0GBZZzGE1e2KDAAg9t1autANgV1nRrS5PMSpzfphT2Cw++xflN25nEYZ1FjESOmqZpzXznvH8QEVgq83XVCs/KMIGsQ22EwitOWWAuIcpdwOYxFAt2a16HgzNSSsRdRJM04vRo08Ph0CX0SlgKK1seIlxCYeq0DodVMbYUVnY8PDPHK810hBvrInwLVVNY2+KRakhGWhGltFsXqyL8CFWfrdJcCGKGwg6vXFfG60hgcXMj6IZ/vDq7DgIDxIbBiAFes9cAH4iaZHmccqZjgW89gONE9a6WgpfMeKGdSFGGwr4wNkJGTDOiT0M9T8sBMVJ9Ej+3kFaCOa2Y7rUE39KI6OdpGfgQdRt4L8GSaq+BovCDqkz7LUWeQ5h4BT3JBu1mKezQ0Z1KDYj5fgozCMf3mHgmRWFekFTYt/Grr773aqdxKV8IAriMeEqESYVDdacv60p//cEWRkgpLEsKFA6Lsbm7BpnruwBRdSy73MutFJquZPiw1O4aGs5kG3hpdMgb5RV2iT0vv4METlAqZPydq5CA6nWwO6hVWPaIxrPDKZnJ39+VjBFXecYYFpCUwmAeW7HJSAnno0oHXsU3ZeoAW4XEeQxqnVQYP5h8UuqTrdVX2aGoiDsye3EFCrPHkifTI1dHU2uUFm3fgKQiUhKduK/Ez/bS8bU95VNnLh3O2/0QpflknMaXOHEAN/vTfzuR7XNIVo/7EK1BuC/2zHT3D7n0p/VMv8edz+fStrKn0hcGHE5dRm+aW4zKR/s8c2ayBep8rmHsn3PFwBn/qysM1G3MfkfRsDMI4zFnvle4oWU+4n4ARKB5+1ww8v3vUsJkDob8MXNcZyc4moLDTX78V5l/8JMuFBjCY04vqJlRXdZ7oBSMj3IqxpPdh35O5Q2Sq1CuD+cpGQPgkWJ2arOuYLtwOp1oiVnf/1U3pG0fHX4cZP1po28Y/9H7KfDou22G1KxAugL+FVYzwmhWAdu9d0L8BkqHAbxOk6mT3kXXxYAM/vW/S8+DAjZPCX1JVnVejcIIn2oul0vPd5ovsTpjvHfq9IU/2rZLj+6tMQVh/qW0X0qpTr3PjBm/C2q/A2g+fRfDD/o7yentCP+qd39+OGNrwzhj/o418bXG76/BGF9YSyFO43mM4zoYl1RSGJMNUhka8R3KqxaR04H21jeO4zj2NUAmhqGYsfX6loEcbY5XLPQTu9cofEc4UrgRfq/sXAmi8F4A60m5H3wtHcCL8fW1O8ZLB7BhPPOh/h1R/aH+bbE3vkvgPwAA//+gIhDAtdFErgAAAABJRU5ErkJggg==
                val imageString = data.split(",")[1]
                val imageData = Base64.decode(imageString, Base64.DEFAULT)
                this.codeBytes.set(imageData)
            }, {error ->
                error.message?.toast()
            })
    }

    fun dismiss(view: View) {
        activity.setResult(Activity.RESULT_CANCELED)
        activity.supportFinishAfterTransition()
    }

    fun onClickCode(view: View) {
        getCode()
    }

    fun onClickSignUp(view: View) {
        RegisterActivity.intentTo(activity)
        activity.finish()
    }

    @SuppressLint("CheckResult")
    fun onClickSignIn(view: View) {
        activity.showLoading()
//        rawApiService.login_post(email.get(), password.get(), code.get())
//                .bindToLifecycle(activity)
//                .subscribeOn(Schedulers.io())
//                .retryWhen(ApiConfig.RetryWithDelay())
//                .map { parseLoginResult(Jsoup.parse(it.string())) }
//                .flatMap {
//                    (code, msg) ->
//                    if (code == 0) {
//                        rawApiService.personal()
//                    } else {
//                        Observable.error(Error(msg))
//                    }
//                }
//                .map { parsePersonal(Jsoup.parse(it.string())) }
//                .observeOn(AndroidSchedulers.mainThread())
//                .doAfterTerminate {
//                    activity.showLogin()
//                }
//                .subscribe({
//                    user.email = email.get()
//                    activity.setResult(Activity.RESULT_OK)
//                    activity.supportFinishAfterTransition()
//                }, {
//                    error ->
//                    error.printStackTrace()
//                    Snackbar.make(activity.binding.container, error.message ?: "登陆失败", Snackbar.LENGTH_SHORT).show()
//                })
        val pwd = MD5Util.crypt(password.get())
        Log.i("LoginViewModel", "user = ${email.get()} pwd = $pwd")
        newApiService.login(email.get(), pwd, code.get())
            .bindToLifecycle(activity)
            .apiResult()
            .doAfterTerminate{
                activity.showLogin()
            }
            .subscribe({ user ->
                if (user.isValid()) {
                    activity.setResult(Activity.RESULT_OK)
                    activity.supportFinishAfterTransition()
                    user.save()
                } else {
                    error("invalid user")
                }
            },{error ->
                error.printStackTrace()
                Snackbar.make(activity.binding.container, error.message ?: "登陆失败", Snackbar.LENGTH_SHORT).show()

            })
    }

    fun onClickForgotPassword(view: View) {
        ForgotPasswordActivity.intentTo(activity)
    }

    fun parseLoginResult(doc: Document): Pair<Int, String>{
        val content = doc.body().text()
        return if ("成功" in content) {
            Pair(0, "")
        } else {
            Pair(1, content)
        }
    }

    private fun parsePersonal(doc: Document): Any {
        // 获取等级
        val lv_a = doc.select("a.lv")[0]
        user.level = lv_a.text().substring(3).toInt()

        // 获取用户名
        val name_div = doc.select("a.name")[0]
        user.name = name_div.text()

        // 获取头像地址
        val index_div = doc.select("div.index")[0]
        val avatar_img = index_div.child(0).child(0)
        user.avatar = avatar_img.attr("src")

        // 获取签名
        val index_table = doc.select("table.index_table")[0]
        val signature_td = index_table.child(0).child(2).child(0)
        user.signature = signature_td.text().removeSuffix(" 更新")

        // 获取短消息
        val message_td = index_table.child(0).child(0).child(3)
        val message = message_td.child(0).child(0).text()
        user.message = if (message == "--") 0 else message.toInt()

        // 获取
        return Object()
    }


}
