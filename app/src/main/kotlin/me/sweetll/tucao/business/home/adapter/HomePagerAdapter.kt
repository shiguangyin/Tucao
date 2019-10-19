package me.sweetll.tucao.business.home.adapter

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import me.sweetll.tucao.business.home.fragment.*
import me.sweetll.tucao.model.json.VideoList

class HomePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val tabTitles = listOf("推荐", "新番", "音乐", "影剧", "游戏", "动画", "频道")

    companion object {
        const val CATE_ANIM = 19
        const val CATE_MUSIC = 20
        const val CATE_GAME = 21
        const val CATE_MOVIE = 23
        const val CATE_NEW = 24
    }

    override fun getItem(position: Int) =
        when (position) {
            0 -> RecommendFragment()
            1 -> VideoListFragment(CATE_NEW)
            2 -> VideoListFragment(CATE_MUSIC)
            3 -> VideoListFragment(CATE_MOVIE)
            4 -> VideoListFragment(CATE_GAME)
            5 -> VideoListFragment(CATE_ANIM)
            else -> ChannelListFragment()
        }

    override fun getCount() = tabTitles.size

    override fun getPageTitle(position: Int) = tabTitles[position]
}
