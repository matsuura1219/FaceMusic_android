package com.example.facemusic.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.facemusic.`interface`.SpotifyListener
import com.example.facemusic.const.Exconst
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState

/** Spotifyの web API を呼び出すクラスです **/

class SpotifyApiUtil: Connector.ConnectionListener {

    /** 定数 **/

    //クライアントID
    private val CLIENT_ID: String = "5e538ac7c7d04d4ba793277b2219df11"
    //リダイレクトURL
    private val REDIRECT_URL: String  = "com.example.facemusic://callback"

    /** 変数 **/

    //Spotifyと接続するためのパラメータをもつインスタンス
    private var connectionParams: ConnectionParams? = null
    //SpotifyAPIを使用するためのインスタンス
    private var spotifyAppRemote: SpotifyAppRemote? = null
    //リスナー
    private var _listener: SpotifyListener? = null


    /** シングルトン **/
    companion object {

        private val _instance: SpotifyApiUtil = SpotifyApiUtil()
        fun getInstance(): SpotifyApiUtil = _instance

    }


    /** Spotifyと接続するためのパラメータを設定する関数です */
    private fun setYourInfo () {
        if (connectionParams == null) {
            connectionParams = ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URL).showAuthView(true).build()
        }

    }

    /** Spotifyと接続しているかを確認する関数です **/
    fun isConnectedToSpotify (): Boolean {

        if (spotifyAppRemote != null) {
            return spotifyAppRemote!!.isConnected
        }

        return false
    }

    /** Spotifyアプリと接続するための関数です **/
    fun connectToSpotifyApp (context: Context, listener: SpotifyListener) {

        _listener = listener

        if (connectionParams == null) {
            //クライアントID、リダイレクトURLを設定したインスタンスをクラス変数に設定します
            setYourInfo()
        }

        //spotifyと接続します
        SpotifyAppRemote.connect(context, connectionParams, this)

    }

    /** 音楽を再生する関数です **/
    fun playMusic (id: String) {

        val url: String = "spotify:track:" + id

        if (spotifyAppRemote != null) {

            if (spotifyAppRemote!!.isConnected) {
                spotifyAppRemote?.playerApi?.play(url)?.setErrorCallback {

                    //曲を再生できなかった場合に実行されます

                }
            }

        }
    }

    /** 音楽を停止する関数です **/
    fun stopMusic () {

        if (spotifyAppRemote != null) {

            spotifyAppRemote?.playerApi?.pause()
        }

    }

    /** Spotifyアプリの接続を解除する関数です **/
    fun desconnectToSpotifyApp () {

        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote)
        }
    }

    /** 接続に成功した場合に実行されるコールバック関数です **/
    override fun onConnected(p0: SpotifyAppRemote?) {

        spotifyAppRemote = p0

        _listener?.onAuthenticationResponse(Exconst.AUTHENTICATION_COMPLETE)

    }


    /** 接続に失敗した場合に実行されるコールバック関数です **/
    override fun onFailure(p0: Throwable?) {

        _listener?.onAuthenticationResponse(Exconst.AUTHENTICATION_FAILED)

    }




}