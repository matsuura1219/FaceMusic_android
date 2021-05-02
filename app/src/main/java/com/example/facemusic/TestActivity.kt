package com.example.facemusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse

class TestActivity : AppCompatActivity() {

    /**定数**/
    private val REQUEST_CODE: Int = 1337

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        //OAuth認証を行います
        //SpotifyUtil.getInstance().authorizeYourApp(this, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {

            var response: AuthenticationResponse = AuthenticationClient.getResponse(resultCode, data)

            when {
                response.type == AuthenticationResponse.Type.TOKEN -> {
                    //アクセストークン
                    var accessToken: String = response.accessToken
                    Log.d("debug", "OKです")
                }
                response.type == AuthenticationResponse.Type.ERROR -> {
                    Log.d("debug", "NGです")
                }
                else -> {
                    Log.d("debug", "その他")
                }
            }
        }
    }

}