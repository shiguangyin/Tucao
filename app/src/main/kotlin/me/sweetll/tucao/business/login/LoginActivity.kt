package me.sweetll.tucao.business.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.ActivityCompat
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import me.sweetll.tucao.R
import me.sweetll.tucao.base.BaseActivity
import me.sweetll.tucao.business.login.viewmodel.LoginViewModel
import me.sweetll.tucao.databinding.ActivityLoginBinding
import me.sweetll.tucao.transition.FabTransform

class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: LoginViewModel


    companion object {
        const val ARG_FAB_COLOR = "fab_color"
        const val ARG_FAB_RES_ID = "fab_res_id"

        fun intentTo(context: Context, requestCode: Int = 1, options: Bundle?) {
            val intent = Intent(context, LoginActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                FabTransform.addExtras(intent, options!!.getInt(ARG_FAB_COLOR), options.getInt(ARG_FAB_RES_ID))
            }
            ActivityCompat.startActivityForResult(context as Activity, intent, requestCode, options)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = LoginViewModel(this)
        binding.viewModel = viewModel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FabTransform.setup(this, binding.container)
        }

        // 不要获取账户列表
        // setupAccountAutocomplete()

        val validEmail = RxTextView.textChanges(binding.emailEdit)
                .map { text -> text.isNotEmpty() }
        val validPassword = RxTextView.textChanges(binding.passwordEdit)
                .map { text -> text.isNotEmpty() }
        val validCode = RxTextView.textChanges(binding.codeEdit)
                .map { text -> text.isNotEmpty() }
        Observables.combineLatest(validEmail, validPassword, validCode, {a, b, c -> a and b and c})
                .distinctUntilChanged()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    enable ->
                    binding.signInBtn.isEnabled = enable
                }
    }


    fun showLoading() {
        TransitionManager.beginDelayedTransition(binding.container)
        viewModel.container.set(View.GONE)
        viewModel.progress.set(View.VISIBLE)
    }

    fun showLogin() {
        TransitionManager.beginDelayedTransition(binding.container)
        viewModel.container.set(View.VISIBLE)
        viewModel.progress.set(View.GONE)
    }
}
