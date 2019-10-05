package me.sweetll.tucao.business.video.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.transition.ArcMotion
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import me.sweetll.tucao.AppApplication
import me.sweetll.tucao.R
import me.sweetll.tucao.base.BaseFragment
import me.sweetll.tucao.model.json.Video
import me.sweetll.tucao.business.home.event.RefreshPersonalEvent
import me.sweetll.tucao.business.login.LoginActivity
import me.sweetll.tucao.business.video.ReplyActivity
import me.sweetll.tucao.business.video.adapter.CommentAdapter
import me.sweetll.tucao.business.video.model.Comment
import me.sweetll.tucao.databinding.FragmentVideoCommentsBinding
import me.sweetll.tucao.di.service.NewApiService
import me.sweetll.tucao.di.service.RawApiService
import me.sweetll.tucao.extension.apiResult
import me.sweetll.tucao.extension.sanitizeHtml
import me.sweetll.tucao.extension.toast
import me.sweetll.tucao.model.other.User
import me.sweetll.tucao.transition.FabTransform
import me.sweetll.tucao.widget.HorizontalDividerBuilder
import org.greenrobot.eventbus.EventBus
import org.jsoup.nodes.Document
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class VideoCommentsFragment : BaseFragment() {
    lateinit var binding: FragmentVideoCommentsBinding
    lateinit var video: Video

    val commentAdapter = CommentAdapter(null)

    var commentId = ""

    var page = 0
    var maxPage = 0

    var canInit = 0

    @Inject
    lateinit var user: User

    @Inject
    lateinit var rawApiService: RawApiService

    @Inject
    lateinit var newApiService: NewApiService

    companion object {
        const val REQUEST_LOGIN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppApplication.get()
            .getUserComponent()
            .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_comments, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        canInit = canInit or 1
        checkInit()
    }

    fun bindVideo(video: Video) {
        this.video = video
        commentId = "content_${video.typeid}-${video.hid}-1"
        canInit = canInit or 2
        checkInit()
    }

    private fun checkInit() {
        if (canInit != 3) {
            return
        }

        commentAdapter.setOnLoadMoreListener({
            loadMoreData()
        }, binding.commentRecycler)

        binding.commentRecycler.layoutManager = LinearLayoutManager(context)
        binding.commentRecycler.adapter = commentAdapter
        binding.commentRecycler.addItemDecoration(
            HorizontalDividerBuilder.newInstance(context!!)
                .setDivider(R.drawable.divider_small)
                .build()
        )

        commentAdapter.setOnItemClickListener { _, view, position ->
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!,
                android.support.v4.util.Pair.create(view, "transition_background"),
                android.support.v4.util.Pair.create(view, "transition_comment"))
            val comment = commentAdapter.getItem(position)!!
            ReplyActivity.intentTo(activity!!, commentId, comment, options.toBundle())
        }
        commentAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.linear_thumb_up) {
                // TODO
//                val comment = commentAdapter.getItem(position)!!
//                if (!comment.support) {
//                    comment.support = true
//                    comment.likes += 1
//                    adapter.notifyItemChanged(position)
//                    rawApiService.support(commentId, comment.id)
//                        .sanitizeHtml {
//                            Object()
//                        }
//                        .subscribe({
//                            // Ignored
//                        }, { error ->
//                            error.printStackTrace()
//                        })
//                }
            }
        }
        (binding.commentRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        binding.clickToLoadImg.setOnClickListener {
            binding.clickToLoadImg.visibility = View.GONE
            binding.swipeRefresh.visibility = View.VISIBLE
            binding.commentFab.show()
            loadData()
        }
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }

        binding.commentFab.setOnClickListener {
            if (user.isValid()) {
                startFabTransform()
            } else {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!, binding.commentFab, "transition_login"
                ).toBundle()
                val intent = Intent(activity, LoginActivity::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    FabTransform.addExtras(intent, ContextCompat.getColor(activity!!, R.color.colorPrimary), R.drawable.ic_comment_white)
                }
                startActivityForResult(intent, REQUEST_LOGIN, options)
            }
        }

        RxTextView.textChanges(binding.commentEdit)
            .map { text -> text.isNotEmpty() }
            .distinctUntilChanged()
            .subscribe { enable ->
                binding.sendCommentBtn.isEnabled = enable
            }

        binding.sendCommentBtn.setOnClickListener {
            binding.commentEdit.isEnabled = false
            binding.sendCommentBtn.isEnabled = false
            binding.sendCommentBtn.text = "发射中"
            val commentInfo = binding.commentEdit.text.toString()
            val lastFloor: Int = commentAdapter.data.getOrNull(0)?.lch?.replace("[\\D]".toRegex(), "")?.toInt()
                ?: 0
            val currentDateTime = System.currentTimeMillis()
            commentAdapter.addData(0, Comment(user.avatar, "lv${user.level}", user.name,
                0, "${lastFloor + 1}楼", currentDateTime, commentInfo, -1, 0, false))
            binding.commentRecycler.smoothScrollToPosition(0)
            rawApiService.sendComment(commentId, commentInfo)
                .bindToLifecycle(this)
                .sanitizeHtml {
                    parseSendCommentResult(this)
                }
                .map { (code, msg) ->
                    if (code == 0) {
                        Object()
                    } else {
                        throw Error(msg)
                    }
                }
                .doAfterTerminate {
                    binding.commentEdit.isEnabled = true
                    binding.sendCommentBtn.isEnabled = true
                    binding.sendCommentBtn.text = "发射"
                }
                .subscribe({
                    // 成功
                    binding.commentEdit.setText("")
                    commentAdapter.data[0].hasSend = true
                    commentAdapter.notifyItemChanged(0)
                }, { error ->
                    // 失败
                    commentAdapter.remove(0)
                    error.printStackTrace()
                    "发送失败，请检查网络".toast()
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOGIN && Activity.RESULT_OK == resultCode) {
            EventBus.getDefault().post(RefreshPersonalEvent())
        }
    }

    fun startFabTransform() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.commentFab.visibility = View.GONE
            binding.commentContainer.visibility = View.VISIBLE

            val startBounds = Rect(binding.commentFab.left, binding.commentFab.top, binding.commentFab.right, binding.commentFab.bottom)
            val endBounds = Rect(binding.commentContainer.left, binding.commentContainer.top, binding.commentContainer.right, binding.commentContainer.bottom)

            val fabColor = ColorDrawable(ContextCompat.getColor(activity!!, R.color.pink_300))
            fabColor.setBounds(0, 0, endBounds.width(), endBounds.height())
            binding.commentContainer.overlay.add(fabColor)

            val circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.commentContainer, binding.commentContainer.width / 2, binding.commentContainer.height / 2,
                binding.commentFab.width / 2f, binding.commentContainer.width / 2f)
            val pathMotion = ArcMotion()
            circularReveal.interpolator = FastOutSlowInInterpolator()
            circularReveal.duration = 240

            val translate = ObjectAnimator.ofFloat(binding.commentContainer, View.TRANSLATION_X, View.TRANSLATION_Y,
                pathMotion.getPath((startBounds.centerX() - endBounds.centerX()).toFloat(), (startBounds.centerY() - endBounds.centerY()).toFloat(), 0f, 0f))
            translate.interpolator = LinearOutSlowInInterpolator()
            translate.duration = 240

            val colorFade = ObjectAnimator.ofInt(fabColor, "alpha", 0)
            colorFade.duration = 120
            colorFade.interpolator = FastOutSlowInInterpolator()

            val transition = AnimatorSet()
            transition.duration = 240
            transition.playTogether(circularReveal, translate, colorFade)
            transition.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    binding.commentContainer.overlay.clear()
                }
            })

            transition.start()
        } else {
            binding.commentFab.hide()
            binding.commentContainer.visibility = View.VISIBLE
        }
    }

    fun loadData() {
        if (!binding.swipeRefresh.isRefreshing) {
            binding.swipeRefresh.isRefreshing = true
        }
        page = 0
        newApiService.videoComments(video.id, page)
            .bindToLifecycle(this)
            .apiResult()
            .doAfterTerminate { binding.swipeRefresh.isRefreshing = false }
            .subscribe({ comments ->
                page++
                commentAdapter.setNewData(comments)
                if (comments.isEmpty()) {
                    val emptyView = LayoutInflater.from(context).inflate(R.layout.layout_empty_comment, null)
                    commentAdapter.emptyView = emptyView
                }
                commentAdapter.loadMoreEnd()
            }, { error ->
                error.printStackTrace()
                error.message?.toast()
            })

    }

    private fun loadMoreData() {
        newApiService.videoComments(video.id, page)
            .bindToLifecycle(this)
            .apiResult()
            .subscribe({ comments ->
                page++
                commentAdapter.addData(comments)
                if (page <= maxPage) {
                    commentAdapter.loadMoreComplete()
                } else {
                    commentAdapter.loadMoreEnd()
                }
            }, { error ->
                error.printStackTrace()
                error.message?.toast()
            })

    }

    private fun parseSendCommentResult(doc: Document): Pair<Int, String> {
        val result = doc.body().text()
        if ("成功" in result) {
            return Pair(0, "")
        } else {
            return Pair(1, result)
        }
    }

}
