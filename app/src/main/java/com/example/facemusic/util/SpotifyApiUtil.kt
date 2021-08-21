package com.example.facemusic.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.facemusic.`interface`.SpotifyAuthListener
import com.example.facemusic.`interface`.SpotifyGetCurrentMusicPosition
import com.example.facemusic.`interface`.SpotifyIsPlayingListener
import com.example.facemusic.const.Exconst
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp
import com.spotify.protocol.types.*
import kotlinx.coroutines.runBlocking

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
    
    //リスナー（認証後に呼ばれるインターフェース）
    private var _listener: SpotifyAuthListener? = null
    //リスナー（曲が再生されているかどうかを判定した後に呼ばれるインターフェース
    private var _isPlayingListener: SpotifyIsPlayingListener? = null
    //リスナー（曲の再生位置を取得した後に呼ばれるインターフェース）
    private var _getCurrentMusicPositionListener: SpotifyGetCurrentMusicPosition? = null

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
    fun connectToSpotifyApp (context: Context, listener: SpotifyAuthListener) {

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


    /** 曲の再生位置を変更する関数です **/
    fun changeMusicPosition (position: Long) {

        if (spotifyAppRemote != null) {

            spotifyAppRemote?.playerApi?.seekToRelativePosition(position)
        }
    }

    /** 曲が再生中かどうかを判定する関数です **/
    fun isPlaying (listener: SpotifyIsPlayingListener) {

        if (spotifyAppRemote != null) {

            spotifyAppRemote!!.playerApi?.playerState.setResultCallback {

                var isPlaying = !(it.isPaused)

                listener.onIsPlayingResponse(isPlaying)

            }

        }

    }

    /** 再生中の曲の情報を取得する関数です **/
    fun getMusicInfo (): Long{

        if (spotifyAppRemote != null) {

            var duration: Long = 0L

            val result = spotifyAppRemote!!.playerApi.playerState.setResultCallback {

                duration = it.track.duration
                /*
                val track = it.track
                val album: Album = track.album
                val artist: Artist = track.artist
                val artists: List<Artist> = track.artists
                val duration: Long = track.duration
                val imageUrl: ImageUri = track.imageUri

                */

                return@setResultCallback

            }

            Log.d("tag", duration.toString())

            return  duration
        }

        return 0L

    }

    //再生中の曲が何ms秒かを取得する関数です
    fun getCurrentMusicPosition (listener: SpotifyGetCurrentMusicPosition) {

        if (spotifyAppRemote != null) {

            spotifyAppRemote!!.playerApi.playerState.setResultCallback {

                var currentPosition: Long = it.playbackPosition

                listener.getCurrentMusicPosition(currentPosition)

            }
        }

    }


    /** Spotifyアプリの接続を解除する関数です **/
    fun desconnectToSpotifyApp () {

        if (spotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(spotifyAppRemote)
        }
    }

    /** 認証時、接続に成功した場合に実行されるコールバック関数です **/

    override fun onConnected(p0: SpotifyAppRemote?) {

        spotifyAppRemote = p0

        _listener?.onAuthenticationResponse(Exconst.AUTHENTICATION_COMPLETE)

    }


    /** 認証時、接続に失敗した場合に実行されるコールバック関数です **/

    override fun onFailure(p0: Throwable?) {

        if (p0 is CouldNotFindSpotifyApp) {

            _listener?.onAuthenticationResponse(Exconst.COULD_NOT_FIND_APP)

        } else {

            _listener?.onAuthenticationResponse(Exconst.AUTHENTICATION_FAILED)
        }

    }




}