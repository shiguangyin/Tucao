package me.sweetll.tucao.business.home.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.sweetll.tucao.R
import me.sweetll.tucao.extension.load
import me.sweetll.tucao.model.json.Video

class VideoListAdapter(videos: List<Video>) : BaseQuickAdapter<Video, BaseViewHolder>(R.layout.item_video_small, videos) {

    override fun convert(helper: BaseViewHolder, item: Video) {
        val imgThumb = helper.getView<ImageView>(R.id.img_thumb)
        val tvPlayCount = helper.getView<TextView>(R.id.text_play)
        val tvTitle = helper.getView<TextView>(R.id.text_title)

        imgThumb.load(mContext, item.thumb)
        tvPlayCount.text = item.play.toString()
        tvTitle.text = item.title
        helper.addOnClickListener(R.id.card)
        helper.setTag(R.id.card, item.id)
        helper.setTag(R.id.text_title, item.thumb)
    }

}