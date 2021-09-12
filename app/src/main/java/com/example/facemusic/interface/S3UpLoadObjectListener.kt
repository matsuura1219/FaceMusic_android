package com.example.facemusic.`interface`

import java.lang.Exception

/** AWS S3に画像送信をした後にリクエストを送信した後に呼ばれるインターフェイスです **/

interface S3UpLoadObjectListener {

    /** 送信後に実行される関数です
     * @param state String ステータスコード
     * @param ex Exception? 例外情報
     */

    fun onS3Uploaded(resultCode: String, ex: Exception?)

}