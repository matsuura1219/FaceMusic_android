package com.example.facemusic.service

import android.content.Context
import com.example.facemusic.R
import com.example.facemusic.`interface`.SpotifyAuthListener
import com.example.facemusic.`interface`.SpotifyGetCurrentMusicPosition
import com.example.facemusic.`interface`.SpotifyIsPlayingListener
import com.example.facemusic.const.Constants
import com.example.facemusic.util.DialogUtil
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.protocol.types.Artist

/** Spotifyの web API を呼び出すクラスです **/

class SpotifyApiClient: Connector.ConnectionListener {

    /** 定数 **/

    //　クライアントID
    private val CLIENT_ID: String = "5e538ac7c7d04d4ba793277b2219df11"
    //　リダイレクトURL
    private val REDIRECT_URL: String  = "com.example.facemusic://callback"

    /** 変数 **/

    //　Spotifyと接続するためのパラメータをもつインスタンス
    private var connectionParams: ConnectionParams? = null
    //　SpotifyAPIを使用するためのインスタンス
    private var spotifyAppRemote: SpotifyAppRemote? = null
    //　認証時のリスナー（保持用）
    private var listener: SpotifyAuthListener? = null

    /*
    //リスナー（認証後に呼ばれるインターフェース）
    private var _listener: SpotifyAuthListener? = null
    //リスナー（曲が再生されているかどうかを判定した後に呼ばれるインターフェース
    private var _isPlayingListener: SpotifyIsPlayingListener? = null
    //リスナー（曲の再生位置を取得した後に呼ばれるインターフェース）
    private var _getCurrentMusicPositionListener: SpotifyGetCurrentMusicPosition? = null
    */

    /** シングルトン **/

    companion object {

        private val instance: SpotifyApiClient =
            SpotifyApiClient()
        fun getInstance(): SpotifyApiClient =
            instance

    }

    /**
     * Spotifyアプリと接続するための関数です
     * @param context Context コンテキスト
     * @param listener SpotifyAuthListener 認証後に呼ばれるリスナー
     */

    fun connectToSpotifyApp (context: Context, listener: SpotifyAuthListener) {

        // グローバル変数に記録します
        this.listener = listener

        if (connectionParams == null) {

            // クライアントID、リダイレクトURLを設定したインスタンスをクラス変数に設定します
            setYourInfo()
        }

        // spotifyと接続します
        SpotifyAppRemote.connect(context, connectionParams, this)

    }

    /** Spotifyと接続するためのパラメータを設定する関数です **/

    private fun setYourInfo () {
        if (connectionParams == null) {
            connectionParams = ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URL).showAuthView(true).build()
        }

    }


    /** Spotifyアプリの接続を解除する関数です **/

    fun disconnectToSpotifyApp () {

        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote)
        }
    }


    /** Spotifyとの接続に成功した場合に実行されるコールバック関数です
     * @param p0 SpotifyAppRemote? AppRemoteライブラリのオブジェクト
     */

    override fun onConnected(p0: SpotifyAppRemote?) {

        spotifyAppRemote = p0

        listener?.onAuthenticationResponse(Constants.AUTHENTICATION_COMPLETE)

    }


    /** Spotifyとの接続に失敗した場合に実行されるコールバック関数です
     * @param po Throwable? 例外情報
     */

    override fun onFailure(p0: Throwable?) {

        if (p0 is CouldNotFindSpotifyApp) {

            // Spotifyアプリが未インストールの場合

            listener?.onAuthenticationResponse(Constants.COULD_NOT_FIND_APP)

        } else {

            //その他認証エラー

            listener?.onAuthenticationResponse(Constants.AUTHENTICATION_FAILED)
        }
    }


    /** Spotifyと接続しているかを確認する関数です **/

    fun isConnectedToSpotify (): Boolean {

        if (spotifyAppRemote != null) {
            return spotifyAppRemote!!.isConnected
        }

        return false
    }



    /** 音楽を再生する関数です
     * @param id String 曲のID
     * @param context Context コンテキスト
     */

    fun playMusic (id: String, context: Context) {

        val url: String = "spotify:track:" + id
        if (spotifyAppRemote != null) {
            if (spotifyAppRemote!!.isConnected) {
                // spotifyと接続されていた場合
                spotifyAppRemote?.playerApi?.play(url)?.setErrorCallback {
                    // 曲を再生できなかった場合に実行されます
                    DialogUtil.getInstance().showErrorMessageToPreviousScreen(context.getString(R.string.cannot_play_music), context.getString(R.string.yes), context)
                }
            }
        }
    }

    /** 音楽を途中から再生する関数です **/

    fun resumePlayMusic () {
        if (spotifyAppRemote != null) {
            spotifyAppRemote?.playerApi?.resume()
        }
    }

    /** 音楽を停止する関数です **/

    fun stopMusic () {
        if (spotifyAppRemote != null) {
            spotifyAppRemote?.playerApi?.pause()
        }
    }


    /** 曲の再生位置を変更する関数です
     * @param position Long 現在から再生したい位置までの相対的な時間
     */
    fun changeMusicPosition (position: Long) {

        if (spotifyAppRemote != null) {

            spotifyAppRemote?.playerApi?.seekToRelativePosition(position)
        }
    }


    /** 再生中の曲が何ms秒かを取得する関数です
     * @param listener SpotifyGetCurrentMusicPosition 現在の再生位置を返却するためのリスナー
     */
    fun getCurrentMusicPosition (listener: SpotifyGetCurrentMusicPosition) {

        if (spotifyAppRemote != null) {

            spotifyAppRemote!!.playerApi.playerState.setResultCallback {

                var currentPosition: Long = it.playbackPosition

                listener.getCurrentMusicPosition(currentPosition)

            }
        }

    }

}