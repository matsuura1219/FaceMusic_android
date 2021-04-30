package com.example.facemusic.`interface`

/** EC2インスタンスからのレスポンス処理を行うインターフェイスです **/

interface EC2ServerListener {

    //レスポンスが返ってきたときに呼ばれる関数です
    fun onSuccess(data: String?)
    //通信に失敗したときに呼ばれる関数です（ネットワークに接続されていない場合）
    fun onFailure()

}