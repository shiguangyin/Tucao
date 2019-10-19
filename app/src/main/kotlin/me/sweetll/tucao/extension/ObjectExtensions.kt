package me.sweetll.tucao.extension

import android.util.Log


/**
 * created by masker on 2019-10-19
 */


fun Any.logInfo(msg: String) {
    Log.i(this.javaClass.simpleName, msg)
}

fun Any.logDebug(msg: String) {
    Log.i(this.javaClass.simpleName, msg)
}