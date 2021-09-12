package com.example.facemusic.`interface`

/** FaceAPIにリクエストを送信した後に呼ばれるインターフェイスです **/

interface FaceApiListener {

    /** レスポンスが返ってきたときに呼ばれる関数です *
     * @param statusCode String httpのステータスコード
     * @param message String 返却メッセージ
     * @param data String? レスポンスデータ
     */

    fun onSuccess(statusCode: String, message: String, data: String?)

    /** 通信に失敗したときに呼ばれる関数です（ネットワークに接続されていない場合） **/

    fun onFailure()

}