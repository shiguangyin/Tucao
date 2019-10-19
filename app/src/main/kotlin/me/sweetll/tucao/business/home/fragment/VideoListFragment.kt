package me.sweetll.tucao.business.home.fragment

import android.annotation.TargetApi
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.ArcMotion
import android.transition.ChangeBounds
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bigkoo.convenientbanner.ConvenientBanner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import me.sweetll.tucao.R
import me.sweetll.tucao.base.BaseFragment
import me.sweetll.tucao.business.channel.ChannelDetailActivity
import me.sweetll.tucao.business.home.adapter.BannerHolder
import me.sweetll.tucao.business.home.adapter.CategoryAdapter
import me.sweetll.tucao.business.home.adapter.VideoListAdapter
import me.sweetll.tucao.business.home.viewmodel.VideoListViewModel
import me.sweetll.tucao.business.video.VideoActivity
import me.sweetll.tucao.databinding.FragmentVideoListBinding
import me.sweetll.tucao.databinding.HeaderVideoListBinding
import me.sweetll.tucao.extension.dp
import me.sweetll.tucao.model.json.VideoList

class VideoListFragment(category: Int) : BaseFragment() {
    lateinit var binding: FragmentVideoListBinding
    private lateinit var headerBinding: HeaderVideoListBinding

    val viewModel = VideoListViewModel(this, category)

    private val videoListAdapter = VideoListAdapter(emptyList())
    private val categoryAdapter = CategoryAdapter(emptyList())

    private var isLoad = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_list, container, false)
        binding.viewModel = viewModel
        binding.loading.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefresh.isEnabled = false
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadData()
        }
        binding.errorStub.setOnInflateListener {
            _, inflated ->
            inflated.setOnClickListener {
                viewModel.loadData()
            }
        }
        setupRecyclerView()
        loadWhenNeed()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initTransition()
        }
    }

    fun setupRecyclerView() {
        headerBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.header_video_list, binding.root as ViewGroup, false)
        headerBinding.viewModel = viewModel
        headerBinding.recyclerView.adapter = categoryAdapter
        headerBinding.recyclerView.layoutManager = GridLayoutManager(activity, 3)
        headerBinding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                val pos = parent.getChildAdapterPosition(view)
                if (pos % 3 > 0) {
                    outRect.left = 0.5f.dp.toInt()
                }
                if (pos / 3 > 0) {
                    outRect.top = 0.5f.dp.toInt()
                }
            }

        })
        headerBinding.recyclerView.addOnItemTouchListener(object : OnItemChildClickListener() {
            override fun onSimpleItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View, position: Int) {
                val cateId = categoryAdapter.data[position]?.id ?: return
                val activity = activity?: return
                if (cateId > 0) {
                    ChannelDetailActivity.intentTo(activity, cateId)
                }
            }

        })
        videoListAdapter.addHeaderView(headerBinding.root)

        binding.bangumiRecycler.layoutManager = GridLayoutManager(activity, 2)
        binding.bangumiRecycler.adapter = videoListAdapter
        binding.bangumiRecycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                val pos = parent.getChildAdapterPosition(view) - videoListAdapter.headerLayoutCount
                if (pos < 0) return
                outRect.bottom = 8.dp
                outRect.left = 8.dp
                if (pos % 2 > 0) {
                    outRect.right = 8.dp
                }
            }
        })
        binding.bangumiRecycler.addOnItemTouchListener(object: OnItemChildClickListener() {
            override fun onSimpleItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                when (view.id) {
                    R.id.card -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            val coverImg = (((view as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0)
                            val titleText = (view.getChildAt(0) as ViewGroup).getChildAt(1)
                            val p1: Pair<View, String> = Pair.create(coverImg, "cover")
                            val p2: Pair<View, String> = Pair.create(titleText, "bg")
                            val cover = titleText.tag as String
                            val options = ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(activity!!, p1, p2)
                            VideoActivity.intentTo(activity!!, view.tag as Int, cover, options.toBundle())
                        } else {
                            VideoActivity.intentTo(activity!!, view.tag as Int)
                        }
                    }
                }

            }
        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun initTransition() {
        val changeBounds = ChangeBounds()

        val arcMotion = ArcMotion()
        changeBounds.pathMotion = arcMotion

        activity!!.window.sharedElementExitTransition = changeBounds
        activity!!.window.sharedElementReenterTransition = null
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        loadWhenNeed()
    }

    fun loadWhenNeed() {
        if (isVisible && userVisibleHint && !isLoad && !binding.swipeRefresh.isRefreshing) {
            viewModel.loadData()
        }
    }



    fun loadVideoList(videoList: VideoList) {
        if (!isLoad) {
            isLoad = true
            TransitionManager.beginDelayedTransition(binding.swipeRefresh)
            binding.swipeRefresh.isEnabled = true
            binding.loading.visibility = View.GONE
            if (binding.errorStub.isInflated) {
                binding.errorStub.root.visibility = View.GONE
            }
            binding.bangumiRecycler.visibility = View.VISIBLE
        }
        videoListAdapter.setNewData(videoList.videos)
        categoryAdapter.setNewData(videoList.categories)
        headerBinding.banner.setPages({ BannerHolder() }, videoList.banners)
            .setPageIndicator(intArrayOf(R.drawable.indicator_white_circle, R.drawable.indicator_pink_circle))
            .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
            .startTurning(3000)
    }

    fun loadError() {
        if (!isLoad) {
            TransitionManager.beginDelayedTransition(binding.swipeRefresh)
            binding.loading.visibility = View.GONE
            if (!binding.errorStub.isInflated) {
                binding.errorStub.viewStub!!.visibility = View.VISIBLE
            } else {
                binding.errorStub.root.visibility = View.VISIBLE
            }
        }
    }

    fun setRefreshing(isRefreshing: Boolean) {
        if (isLoad) {
            binding.swipeRefresh.isRefreshing = isRefreshing
        } else {
            TransitionManager.beginDelayedTransition(binding.swipeRefresh)
            binding.loading.visibility = if (isRefreshing) View.VISIBLE else View.GONE
            if (isRefreshing) {
                if (!binding.errorStub.isInflated) {
                    binding.errorStub.viewStub!!.visibility = View.GONE
                } else {
                    binding.errorStub.root.visibility = View.GONE
                }
            }
        }
    }
}