package me.sweetll.tucao.business.channel

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.AdapterView
import me.sweetll.tucao.R
import me.sweetll.tucao.base.BaseActivity
import me.sweetll.tucao.business.channel.adapter.ChannelFilterAdapter
import me.sweetll.tucao.business.channel.adapter.ChannelPagerAdapter
import me.sweetll.tucao.business.channel.event.ChangeChannelFilterEvent
import me.sweetll.tucao.business.channel.model.ChannelFilter
import me.sweetll.tucao.business.channel.viewmodel.ChannelDetailViewModel
import me.sweetll.tucao.databinding.ActivityChannelDetailBinding
import me.sweetll.tucao.model.json.Channel
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

class ChannelDetailActivity : BaseActivity() {
    lateinit var binding: ActivityChannelDetailBinding
    val detailViewModel = ChannelDetailViewModel(this)

    var tid = 0
    lateinit var channel: Channel
    lateinit var parentChannel: Channel
    lateinit var siblingChannels: List<Channel>

    companion object {
        private val ARG_TID = "arg_tid"

        fun intentTo(activity: Activity, tid: Int) {
            activity.startActivity<ChannelDetailActivity>(ARG_TID to tid)
        }
    }

    override fun getToolbar(): Toolbar = binding.toolbar

    override fun getStatusBar(): View? = binding.statusBar

    override fun initView(savedInstanceState: Bundle?) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_channel_detail)
        binding.viewModel = detailViewModel

        tid = intent.getIntExtra(ARG_TID, 0)
        channel = Channel.find(tid)!!
        parentChannel = Channel.find(channel.getValidParentId())!!
        siblingChannels = Channel.findSiblingChannels(parentChannel.id)

        val channelFilterAdapter = ChannelFilterAdapter(this,
                mutableListOf(
                        ChannelFilter(parentChannel.name, "最近发布", "date"),
                        ChannelFilter(parentChannel.name, "人气最旺", "views"),
                        ChannelFilter(parentChannel.name, "弹幕最多", "danmakuCount")
                )
        )
        binding.spinner.adapter = channelFilterAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val item = parent.getItemAtPosition(position) as ChannelFilter
                EventBus.getDefault().post(ChangeChannelFilterEvent(item.sort))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.viewPager.adapter = ChannelPagerAdapter(supportFragmentManager, siblingChannels)
        binding.viewPager.offscreenPageLimit = siblingChannels.size
        binding.tab.setupWithViewPager(binding.viewPager)
        val selectedTabPosition = siblingChannels.indexOf(channel)
        binding.tab.getTabAt(selectedTabPosition)?.select()
    }

    override fun initToolbar() {
        super.initToolbar()
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }
}
