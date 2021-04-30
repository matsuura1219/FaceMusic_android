package com.example.facemusic.util

import android.app.Activity
import android.content.Context
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse

/** Spotifyの web API を呼び出すクラスです **/

/*
class SpotifyUtil {

    //クライアントID
    private val CLIENT_ID: String = "5e538ac7c7d04d4ba793277b2219df11"
    //リダイレクトURL
    private val REDIRECT_URL: String  = "com.example.facemusic://callback"

    companion object {

        private val _instace: SpotifyUtil = SpotifyUtil()
        fun getInstance(): SpotifyUtil = _instace

    }

    fun authorizeYourApp (activity: Activity, requestCode: Int) {

        var builder: AuthenticationRequest.Builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URL)

        builder.setScopes(arrayOf("streaming"))

        var request: AuthenticationRequest = builder.build()

        AuthenticationClient.openLoginActivity(activity, requestCode, request)

    }
}
*/