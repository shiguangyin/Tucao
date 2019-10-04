package me.sweetll.tucao.util

import android.graphics.Color
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.parser.android.AndroidFileSource
import master.flame.danmaku.danmaku.util.DanmakuUtils
import me.sweetll.tucao.model.json.DanmakuItem
import me.sweetll.tucao.model.json.NewBaseResp
import okio.Okio

//parse danmaku from json
class NewDanmakuParser: BaseDanmakuParser() {

    companion object {
        const val TAG = "NewDanmakuParser"
    }

    private var index = 0

    override fun parse(): IDanmakus? {
        val dataSource = mDataSource as? AndroidFileSource ?: return null
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, DanmakuItem::class.java)
        val type = Types.newParameterizedType(NewBaseResp::class.java, listType)
        val adapter = moshi.adapter<NewBaseResp<List<DanmakuItem>>>(type)
        val resp = adapter.fromJson(Okio.buffer(Okio.source(dataSource.data()))) ?: return null
        val data = resp.data ?: return null
        if (resp.code != 0 || data.isEmpty()) {
            return null
        }
        val danmakus = Danmakus()
        data.forEach forEach@{ item ->
            val danmaku = mContext.mDanmakuFactory.createDanmaku(item.type, mContext) ?: return@forEach
            val color = ((0x00000000ff000000 or item.color.toLong()) and 0x00000000ffffffff).toInt()
            with(danmaku) {
                time = (item.time * 1000).toLong()
                textSize = item.fontSize * (mDispDensity - 0.6f)
                textColor = color
                textShadowColor = if (textColor <= Color.BLACK) Color.WHITE else Color.BLACK
                timer = mTimer
                flags = mContext.mGlobalFlagValues
            }
            DanmakuUtils.fillText(danmaku, item.text)
            danmaku.index = index++
            danmakus.addItem(danmaku)
        }
        Log.i(TAG, "result size = ${danmakus.size()}")
        return danmakus
    }

}