package com.example.facemusic.`interface`

interface SpotifyGetCurrentMusicPosition {

    /** 曲の再生位置を取得した後に呼ばれるコールバック関数です **/
    fun getCurrentMusicPosition (position: Long)
}