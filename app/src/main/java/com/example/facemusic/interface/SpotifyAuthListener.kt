package com.example.facemusic.`interface`

/** SpotifyのAPIにリクエストを送信した後に呼ばれるインターフェイスです **/

interface SpotifyAuthListener {

    /** 認証時に実行されるコールバック関数です
     * @param resultCode String ステータスコード
     */

    fun onAuthenticationResponse(resultCode: String)

}