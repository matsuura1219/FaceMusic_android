package com.example.facemusic.util

/* Spotifyの web API を呼び出すクラスです */

class SpotifyApiUtil {

    //シングルトン
    companion object {

        private val _instance: SpotifyApiUtil = SpotifyApiUtil()
        fun getInstance(): SpotifyApiUtil = _instance

    }

}