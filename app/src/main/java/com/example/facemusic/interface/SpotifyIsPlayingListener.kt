package com.example.facemusic.`interface`

/** 曲が再生されているかどうかを判定した後に呼ばれるインターフェイスです **/

interface SpotifyIsPlayingListener {

    //再生中かどうか判定された後に実行されるコールバック関数です
    fun onIsPlayingResponse(isPlaying: Boolean)

}