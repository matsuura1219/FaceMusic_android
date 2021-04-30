package com.example.facemusic.`interface`

/** SpotifyのAPIにリクエストを送信した後に呼ばれるインターフェイスです **/

interface SpotifyListener {

    //認証時に実行されるコールバック関数です
    fun onAuthenticationResponse(resultCode: String)

}