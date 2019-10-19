package me.sweetll.tucao.extension

import android.content.res.Resources

fun Int.formatByWan(): String {
    if (this < 10000) {
        return this.toString()
    } else {
        return "%.1f万".format(this / 10000f)
    }
}

fun Int.formatDanmuSizeToString(): String = String.format("%.2f", this.formatDanmuSizeToFloat())
fun Int.formatDanmuSizeToFloat(): Float = (this + 50) / 100f

fun Int.formatDanmuOpacityToString(): String = String.format("%d%%", this + 20)
fun Int.formatDanmuOpacityToFloat(): Float = (this + 20) / 100f

fun Int.formatDanmuSpeedToString(): String = String.format("%.2f", this.formatDanmuSpeedToFloat())
fun Int.formatDanmuSpeedToFloat(): Float = (this + 30) / 100f


val Float.dp: Float
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()


val Float.sp: Float
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)


val Int.sp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()