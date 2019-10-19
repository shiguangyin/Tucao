package me.sweetll.tucao.business.home.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import me.sweetll.tucao.R
import me.sweetll.tucao.model.json.Category


/**
 * created by masker on 2019-10-19
 */


class CategoryAdapter(data: List<Category>) : BaseQuickAdapter<Category, BaseViewHolder>(R.layout.item_home_video_category, data) {

    override fun convert(helper: BaseViewHolder, item: Category) {
        helper.setText(R.id.tv_name, item.name)
            .addOnClickListener(R.id.tv_name)
    }

}