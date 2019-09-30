package me.sweetll.tucao.model.json


open class NewBaseResp<T> {
    var code: String = ""
    var msg: String = ""
    var data: T? = null

}