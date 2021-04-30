package com.example.facemusic.const

class Exconst {

    companion object {

        //httpレスポンスの正常コード
        val STATUS_CODE_NORMAL: String = "200"

        //Amazon S3への画像送信のステータスコードの定義
        //成功
        val UPLOADED_COMPLETE: String = "1"
        //失敗
        val UPLOADED_FAILED: String = "0"

        //Spotify APIの認証のステータスコードの定義
        //成功
        val AUTHENTICATION_COMPLETE: String = "1"
        //失敗
        val AUTHENTICATION_FAILED: String = "0"

    }
}