package me.sweetll.tucao.business.home

import android.accounts.AccountManager
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.text.method.ScrollingMovementMethod
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.sweetll.tucao.AppApplication
import me.sweetll.tucao.R
import me.sweetll.tucao.base.BaseActivity
import me.sweetll.tucao.business.download.DownloadActivity
import me.sweetll.tucao.business.home.adapter.HomePagerAdapter
import me.sweetll.tucao.business.home.event.RefreshPersonalEvent
import me.sweetll.tucao.business.login.LoginActivity
import me.sweetll.tucao.business.personal.PersonalActivity
import me.sweetll.tucao.business.search.SearchActivity
import me.sweetll.tucao.databinding.ActivityMainBinding
import me.sweetll.tucao.di.service.JsonApiService
import me.sweetll.tucao.di.service.RawApiService
import me.sweetll.tucao.extension.load
import me.sweetll.tucao.extension.toast
import me.sweetll.tucao.model.other.User
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        const val LOGIN_REQUEST = 1

        const val NOTIFICATION_ID = 10
    }

    private val notifyMgr by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    lateinit var binding : ActivityMainBinding
    lateinit var drawerToggle: ActionBarDrawerToggle

    private var lastBackTime = 0L

    @Inject
    lateinit var jsonApiService: JsonApiService

    @Inject
    lateinit var rawApiService: RawApiService

    @Inject
    lateinit var user: User

    lateinit var accountManager: AccountManager

    lateinit var avatarImg: ImageView

    lateinit var usernameText: TextView

    lateinit var messageMenu: MenuItem

    lateinit var messageCounter: TextView


    lateinit var downloadUrl: String

    lateinit var apkFile: File

    override fun getToolbar(): Toolbar = binding.toolbar

    fun initDialog() {
        val updateView = LayoutInflater.from(this).inflate(R.layout.dialog_update, null)
        val descriptionText = updateView.findViewById<TextView>(R.id.text_description)
        descriptionText.movementMethod = ScrollingMovementMethod()
    }

    override fun initView(savedInstanceState: Bundle?) {
        AppApplication.get()
                .getUserComponent()
                .inject(this)

        EventBus.getDefault().register(this)

        initDialog()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupDrawer()

        initCounter()

        accountManager = AccountManager.get(this)

        binding.viewPager.adapter = HomePagerAdapter(supportFragmentManager)
        binding.viewPager.offscreenPageLimit = 6
        binding.tab.setupWithViewPager(binding.viewPager)

        val headerView = binding.navigation.getHeaderView(0)
        avatarImg = headerView.findViewById(R.id.img_avatar)
        usernameText = headerView.findViewById(R.id.text_username)

        doRefresh()

        avatarImg.setOnClickListener {
            if (!user.isValid()) {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this, avatarImg, "transition_login"
                ).toBundle() ?: Bundle()
                options.putInt(LoginActivity.ARG_FAB_COLOR, ContextCompat.getColor(this, R.color.colorPrimary))
                options.putInt(LoginActivity.ARG_FAB_RES_ID, R.drawable.default_avatar)
                LoginActivity.intentTo(this, LOGIN_REQUEST, options)
            } else {
                PersonalActivity.intentTo(this)
//                logoutDialog.show()
            }
        }

    }


    fun setupDrawer() {
        binding.navigation.setNavigationItemSelectedListener {
            menuItem ->
            when (menuItem.itemId) {
                R.id.nav_star -> {
                    StarActivity.intentTo(this)
                }
                R.id.nav_play_history -> {
                    PlayHistoryActivity.intentTo(this)
                }
                R.id.nav_download -> {
                    DownloadActivity.intentTo(this)
                }
                R.id.nav_message -> {
                    MessageListActivity.intentTo(this)
                }
                R.id.nav_setting -> {
                    "没什么好设置的啦( ﾟ∀ﾟ)".toast()
                }
                R.id.nav_about -> {
                    AboutActivity.intentTo(this)
                }
            }
            binding.drawer.closeDrawers()
            true
        }
        drawerToggle = ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.drawer_open, R.string.drawer_close)
        binding.drawer.addDrawerListener(drawerToggle)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        } else {
            when (item.itemId) {
                android.R.id.home -> {
                    binding.drawer.openDrawer(GravityCompat.START)
                    return true
                }
                R.id.action_search -> {
                    val searchView = getToolbar().findViewById<View>(R.id.action_search)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, searchView,
                            "transition_search_back").toBundle()
                    SearchActivity.intentTo(this, options = options)
                    return true
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }
        }
    }

    private fun initCounter() {
        messageMenu = binding.navigation.menu.findItem(R.id.nav_message)
        messageCounter = messageMenu.actionView as TextView
        messageCounter.gravity = Gravity.CENTER_VERTICAL
        messageCounter.setTypeface(null, Typeface.BOLD)
        messageCounter.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        messageCounter.visibility = View.INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST && resultCode == Activity.RESULT_OK) {
            doRefresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPersonal(event: RefreshPersonalEvent) {
        doRefresh()
    }

    private fun doRefresh() {
        if (user.isValid()) {
            avatarImg.load(this, user.avatar, R.drawable.default_avatar, User.signature())
            usernameText.text = user.name
            if (user.message > 0) {
                messageCounter.text = "${user.message}"
                messageCounter.visibility = View.VISIBLE
            } else {
                messageCounter.visibility = View.INVISIBLE
            }
            messageMenu.isVisible = true
        } else {
            usernameText.text = "点击头像登录"
            messageMenu.isVisible = false
            Glide.with(this)
                    .load(R.drawable.default_avatar)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarImg)
        }
    }

    override fun onBackPressed() {
        val currentBackTime = System.currentTimeMillis()
        if (currentBackTime - lastBackTime < 2000) {
            super.onBackPressed()
        } else {
            lastBackTime = currentBackTime
            "再按一次退出".toast()
        }
    }
}
