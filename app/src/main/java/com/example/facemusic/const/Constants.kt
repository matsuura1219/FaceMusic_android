package com.example.facemusic.const

import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp

class Constants {

    companion object {

        /** HTTP ステータスコード **/
        // httpレスポンスの正常コード
        val STATUS_CODE_NORMAL: String = "200"

        /** Amazon S3 コード定義 **/

        // Amazon S3への画像送信のステータスコードの定義
        // 成功
        val UPLOADED_COMPLETE: String = "1"
        // 失敗
        val UPLOADED_FAILED: String = "0"

        /** Spotify コード定義 **/

        // Spotify APIの認証のステータスコードの定義
        // 認証成功
        val AUTHENTICATION_COMPLETE: String = "1"
        // 認証失敗（その他）
        val AUTHENTICATION_FAILED: String = "0"
        // 認証失敗（アプリ未インストール）
        val COULD_NOT_FIND_APP: String = "9"


        /** FaceAPI コード定義 **/
        // 正常レスポンス
        val SUCCESS_FACE_API: String = "OK"

        /** その他コード **/
        // EMOTION_DETECTIONのコード
        var EMOTION_DETECTION: String = "0"
        // AGE_DETECTIONのコード
        val AGE_DETECTION: String = "1"


        // 提案された楽曲の選択時、遷移の際に渡すKEY
        val KEY: String = "KEY"
        // 提案された楽曲の選択時、遷移の際に渡すVALUE
        val SNACKBAR_CLICK: String = "SNACKBAR_CLICK"
        // ListViewからの遷移時に渡すキー
        val LISTVIEW_CLICK: String = "LISTVIEW_CLICK"

    }
}