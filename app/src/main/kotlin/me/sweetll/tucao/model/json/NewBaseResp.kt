package me.sweetll.tucao.model.json


open class NewBaseResp<T> {
    var code: Int = -1
    var msg: String = ""
    var data: T? = null

}