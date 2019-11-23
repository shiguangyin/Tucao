package me.sweetll.tucao.di.component

import dagger.Subcomponent
import me.sweetll.tucao.business.channel.fragment.ChannelDetailFragment
import me.sweetll.tucao.business.splash.SplashActivity
import me.sweetll.tucao.di.module.ApiModule
import me.sweetll.tucao.di.module.UserModule
import me.sweetll.tucao.di.scope.ApplicationScope
import me.sweetll.tucao.extension.DownloadHelpers

@ApplicationScope
@Subcomponent(modules = arrayOf(ApiModule::class))
interface ApiComponent {
    fun plus(userModule: UserModule): UserComponent

    fun inject(channelDetailFragment: ChannelDetailFragment)
    fun inject(serviceInstance: DownloadHelpers.ServiceInstance)
    fun inject(splashActivity: SplashActivity)
}
