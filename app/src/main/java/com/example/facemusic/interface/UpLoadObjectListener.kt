package com.example.facemusic.`interface`

import java.lang.Exception

/** AWS S3に画像送信をした後にリクエストを送信した後に呼ばれるインターフェイスです **/

interface UpLoadObjectListener {

    //送信後に実行される関数です
    fun onUploaded(state: String, ex: Exception?)

}