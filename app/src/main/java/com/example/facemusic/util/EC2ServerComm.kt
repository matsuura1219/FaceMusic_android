package com.example.facemusic.util

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/** EC2インスタンスと接続を行うクラスです **/

class EC2ServerComm {

    /** 定数 **/
    private val EMOTION_URL = ""

    /** 変数 **/

    /** シングルトン **/
    companion object {
        private var _instance: EC2ServerComm = EC2ServerComm()
        fun getInstance(): EC2ServerComm = _instance
    }

    /** 感情データを送信し、最適な楽曲を取得する関数です (GET) **/
    fun getMusicForEmtions (anger: Float, contempt: Float, disgust: Float, fear: Float, happiness: Float, neutral: Float, sadness: Float, surprise: Float) {

        //インスタンス化
        val client = OkHttpClient()

        var url =
            "$EMOTION_URL?anger=$anger&contempt=$contempt&disgust=$disgust&fear=$fear&happiness=$happiness&neutral=$neutral&sadness=$sadness&surprise=$surprise"

        val request: Request = Request.Builder().url(url).build()


        //POST通信を行います
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //ネットワークにつながっていない際に呼ばれます

            }

            override fun onResponse(call: Call, response: Response) {
                //レスポンスが返却された際に呼ばれます

            }
        })
    }

}