package com.example.facemusic.util

import android.util.Log
import com.example.facemusic.`interface`.EC2ServerListener
import com.example.facemusic.const.Exconst
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/** EC2インスタンスと接続を行うクラスです **/

class EC2ServerComm {

    /** 定数 **/
    private val EMOTION_URL = "http://18.179.205.80:8080/FaceMusic-Server/EmotionsApi"
    private val AGE_URL = "http://18.179.205.80:8080/FaceMusic-Server/AgeApi"

    /** 変数 **/

    /** シングルトン **/
    companion object {
        private var _instance: EC2ServerComm = EC2ServerComm()
        fun getInstance(): EC2ServerComm = _instance
    }

    /** 感情データを送信し、最適な楽曲を取得する関数です (GET) **/
    fun getMusicForEmtions (anger: Float, contempt: Float, disgust: Float, fear: Float, happiness: Float, neutral: Float, sadness: Float, surprise: Float, listener: EC2ServerListener) {

        //インスタンス化
        val client = OkHttpClient()

        var emotionUrl =
            "$EMOTION_URL?anger=$anger&contempt=$contempt&disgust=$disgust&fear=$fear&happiness=$happiness&neutral=$neutral&sadness=$sadness&surprise=$surprise"

        val request: Request = Request.Builder().url(emotionUrl).build()

        //GET通信を行います
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //ネットワークにつながっていない際に呼ばれます
                Log.d("debug", "onFailure")

                //呼び出し元Activityに通知します
                listener.onFailure()

            }

            override fun onResponse(call: Call, response: Response) {
                //レスポンスが返却された際に呼ばれます
                Log.d("debug", "onSuccess")

                //ステータスコード
                val statusCode: Int = response.code
                //json文字列
                val jsonStr = response.body?.string()

                if (statusCode == Integer.parseInt(Exconst.STATUS_CODE_NORMAL)) {

                    //呼び出し元Activityに通知します
                    listener.onSuccess(jsonStr)

                } else {

                    //呼び出し元Activityに通知します
                    listener.onFailure()
                }

            }
        })
    }

    fun getMusicForAges (age: Float, gender: String?, listener: EC2ServerListener) {

        //インスタンス化
        val client = OkHttpClient()

        var url =
            "$AGE_URL?age=$age&gender=$gender"

        val request: Request = Request.Builder().url(url).build()

        //GET通信を行います
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //ネットワークにつながっていない際に呼ばれます
                Log.d("debug", "onFailure")

                //呼び出し元Activityに通知します
                listener.onFailure()

            }

            override fun onResponse(call: Call, response: Response) {
                //レスポンスが返却された際に呼ばれます
                Log.d("debug", "onSuccess")

                //ステータスコード
                val statusCode: Int = response.code
                //json文字列
                val jsonStr = response.body?.string()

                if (statusCode == Integer.parseInt(Exconst.STATUS_CODE_NORMAL)) {

                    //呼び出し元Activityに通知します
                    listener.onSuccess(jsonStr)

                } else {

                    //呼び出し元Activityに通知します
                    listener.onFailure()
                }

            }
        })

    }

}