package com.example.facemusic.service

import android.util.Log
import com.example.facemusic.`interface`.EC2ServerListener
import com.example.facemusic.const.Constants
import okhttp3.*
import java.io.IOException

/** EC2インスタンスと接続を行うクラスです **/

class EC2Client {

    /** 定数 **/

    // サーバのURL
    private val SERVER_URL: String = "http://18.179.205.80:8080/FaceMusic-Server"
    //EmotionDetectionのパス
    private val EMOTION_URL = "/EmotionsApi"
    // AgeDetectionのパス
    private val AGE_URL = "/AgeApi"

    /** 変数 **/

    /** シングルトン **/

    companion object {
        private var instance: EC2Client = EC2Client()
        fun getInstance(): EC2Client = instance
    }

    /** 感情データを送信し、最適な楽曲を取得する関数です (GET)
     * @param anger Float 怒り
     * @param contempt Float 軽蔑
     * @param disgust Float 嫌悪
     * @param fear Float 恐怖
     * @param happiness FLoat 幸福
     * @param neutral Float 自然
     * @param sadness Float 悲しさ
     * @param surprise Float 驚き
     * @param from Int DB検索のスタート行
     * @param to Int DB検索のファイナル行
     * @param listener EC2ServerListener EC2からのレスポンスのリスナー
     */

    fun getMusicForEmtion (anger: Float, contempt: Float, disgust: Float, fear: Float, happiness: Float, neutral: Float, sadness: Float, surprise: Float, from: Int, to: Int, listener: EC2ServerListener) {

        // インスタンス化
        val client = OkHttpClient()

        var emotionUrl =
            "$SERVER_URL$EMOTION_URL?anger=$anger&contempt=$contempt&disgust=$disgust&fear=$fear&happiness=$happiness&neutral=$neutral&sadness=$sadness&surprise=$surprise"

        val request: Request = Request.Builder().url(emotionUrl).build()

        // GET通信を行います
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                // ネットワークにつながっていない際に呼ばれます

                // 呼び出し元Activityに通知します
                listener.onFailure()

            }

            override fun onResponse(call: Call, response: Response) {
                // レスポンスが返却された際に呼ばれます

                // ステータスコード
                val statusCode: Int = response.code
                // json文字列
                val jsonStr = response.body?.string()

                if (statusCode == Integer.parseInt(Constants.STATUS_CODE_NORMAL)) {

                    // 呼び出し元Activityに通知します
                    listener.onSuccess(jsonStr)

                } else {

                    // 呼び出し元Activityに通知します
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

                if (statusCode == Integer.parseInt(Constants.STATUS_CODE_NORMAL)) {

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