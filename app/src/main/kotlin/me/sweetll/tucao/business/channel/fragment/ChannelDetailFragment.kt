package me.sweetll.tucao.business.channel.fragment

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import me.sweetll.tucao.AppApplication
import me.sweetll.tucao.R
import me.sweetll.tucao.base.BaseFragment
import me.sweetll.tucao.business.channel.adapter.VideoAdapter
import me.sweetll.tucao.business.channel.event.ChangeChannelFilterEvent
import me.sweetll.tucao.model.json.Video
import me.sweetll.tucao.business.video.VideoActivity
import me.sweetll.tucao.databinding.FragmentChannelDetailBinding
import me.sweetll.tucao.di.service.JsonApiService
import me.sweetll.tucao.di.service.NewApiService
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.sanitizeJsonList
import me.sweetll.tucao.extension.toast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class ChannelDetailFragment : BaseFragment() {
    lateinit var binding: FragmentChannelDetailBinding
    var tid = 0

    val videoAdapter = VideoAdapter(null)

    var pageIndex = 0
    val pageSize = 10

    var order = "date"

    @Inject
    lateinit var jsonApiService: JsonApiService

    @Inject
    lateinit var  newApiService: NewApiService

    companion object {
        private val ARG_TID = "tid"

        fun newInstance(tid: Int) : ChannelDetailFragment {
            val fragment = ChannelDetailFragment()
            val args = Bundle()
            args.putInt(ARG_TID, tid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tid = arguments!!.getInt(ARG_TID, 0)

        AppApplication.get()
                .getApiComponent()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_channel_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoAdapter.setOnLoadMoreListener ({
            loadMoreData()
        }, binding.videoRecycler)
        binding.videoRecycler.addOnItemTouchListener(object : OnItemClickListener() {
            override fun onSimpleItemClick(helper: BaseQuickAdapter<*, *>, itemView: View, position: Int) {
                val video: Video = videoAdapter.getItem(position)!!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val coverImg = itemView.findViewById<ImageView>(R.id.img_thumb)
                    val titleText = itemView.findViewById<View>(R.id.text_title)
                    val p1: Pair<View, String> = Pair.create(coverImg, "cover")
                    val cover = titleText.tag as String
                    val options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(activity!!, p1)
                    VideoActivity.intentTo(activity!!, video, cover, options.toBundle()!!)
                } else {
                    VideoActivity.intentTo(activity!!, video)
                }
            }
        })
        binding.videoRecycler.layoutManager = LinearLayoutManager(activity)
        binding.videoRecycler.adapter = videoAdapter
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }

        loadData()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun loadData() {
        if (!binding.swipeRefresh.isRefreshing) {
            binding.swipeRefresh.isRefreshing = true
        }
        pageIndex = 0
        newApiService.getVideoList(tid, pageIndex, pageSize, order)
            .bindToLifecycle(this)
            .apiResult()
            .doAfterTerminate { binding.swipeRefresh.isRefreshing = false }
            .subscribe({
                data ->
                pageIndex++
                videoAdapter.setNewData(data.videos)
            }, {
                error ->
                error.message?.toast()
            })
    }

    private fun loadMoreData() {
        newApiService.getVideoList(tid, pageIndex, pageSize, order)
            .bindToLifecycle(this)
            .apiResult()
            .doAfterTerminate { binding.swipeRefresh.isRefreshing = false }
            .subscribe({
                data ->
                pageIndex++
                videoAdapter.addData(data.videos)
                if (data.videos.size < pageSize) {
                    videoAdapter.loadMoreEnd()
                } else {
                    videoAdapter.loadMoreComplete()
                }
            }, {
                error ->
                error.message?.toast()
                videoAdapter.loadMoreFail()
            })
    }

    @Subscribe
    fun onChangeChannelFilterEvent(event: ChangeChannelFilterEvent) {
        if (order != event.order) {
            order = event.order
            loadData()
        }
    }


}

