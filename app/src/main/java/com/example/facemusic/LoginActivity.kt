package com.example.facemusic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.facemusic.`interface`.SpotifyListener
import com.example.facemusic.const.Exconst
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_login.*

/** Spotifyへ認証を行うためのActivityです **/

class LoginActivity : Activity(), SpotifyListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {

            //Spotifyアプリと接続をします
            SpotifyApiUtil.getInstance().connectToSpotifyApp(this, this)

        }
    }

    /** Spotifyとの認証が完了した時に実行されるコールバック関数です */
    override fun onAuthenticationResponse(resultCode: String) {

        if (resultCode.equals(Exconst.AUTHENTICATION_COMPLETE)) {
            //認証に成功したとき

            //画面遷移を行います
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

        } else {
            //認証に失敗したとき

        }
    }
}