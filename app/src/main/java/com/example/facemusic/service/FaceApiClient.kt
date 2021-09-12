package com.example.facemusic.service

import com.example.facemusic.`interface`.FaceApiListener
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import okhttp3.RequestBody.Companion.toRequestBody

/** サーバ通信を行うためのクラスです **/

class FaceApiClient {

    /** 変数（Face API） **/

    //リスナー
    private var listerforFaceApi: FaceApiListener? = null

    /** 定数 **/
    // エンドポイント
    private val FACE_ENDPOINT = "facemusicapp.cognitiveservices.azure.com"
    // サブスクリプションキー
    private val FACE_SUBSCRIPTION_KEY = "694dff107a7c4ffb98645ec06f2f0723"
    // 画像URL（テスト用）
    private val IMAGE_URL = "https://my-website-v1.s3-ap-northeast-1.amazonaws.com/img/about.jpg"
    // サーバーURL
    private val REQUEST_URL =
        "https://facemusicapp.cognitiveservices.azure.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise&recognitionModel=recognition_03&returnRecognitionModel=false&detectionModel=detection_01&faceIdTimeToLive=86400"


    /** シングルトン **/

    companion object {
        // シングルトンインスタンスの宣言
        private var instance: FaceApiClient =
            FaceApiClient()
        // インスタンス取得
        fun getInstance(): FaceApiClient {
            return instance
        }
    }


    /**
     * curl
     * -H
     * "Ocp-Apim-Subscription-Key:　TODO_INSERT_YOUR_FACE_SUBSCRIPTION_KEY_HERE"
     * "TODO_INSERT_YOUR_FACE_ENDPOINT_HERE/face/v1.0/detect?detectionModel=detection_01&returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise"
     * -H "Content-Type: application/json"
     *
     * --data-ascii
     * "{\"url\":\"https://upload.wikimedia.org/wikipedia/commons/c/c3/RH_Louise_Lillian_Gish.jpg\"}"
     * */

    /**
     * httpの中身です
     * https://japaneast.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise&recognitionModel=recognition_03&returnRecognitionModel=false&detectionModel=detection_01&faceIdTimeToLive=86400 HTTP/1.1
     * Host: japaneast.api.cognitive.microsoft.com
     * Content-Type: application/json
     * Ocp-Apim-Subscription-Key: ••••••••••••••••••••••••••••••••
     * {
     * "url": "https://my-website-v1.s3-ap-northeast-1.amazonaws.com/img/about.jpg"
     * }
     *
     */

    /** Face APIを呼び出し、顔情報を取得するメソッドです
     * @param url String サーバ （Azure） URL
     * @param listener FaceApiListener FaceAPI返却後のリスナー
     */

    fun sendImageToMicroSoft(url: String, listener: FaceApiListener) {

        //　インスタンス化
        val client = OkHttpClient()

        //　bodyに付与するjsonオブジェクト（key:"url" value:"画像URL"）
        val json = JSONObject()
        json.put("url", url)

        //　mediatypeを指定し、エンコーディングする
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        //　POST通信用のリクエストを生成します
        val request = Request.Builder().url(REQUEST_URL)
            .addHeader("Ocp-Apim-Subscription-Key", FACE_SUBSCRIPTION_KEY)
            .addHeader("Host", FACE_ENDPOINT)
            .addHeader("Content-Type", "application/json")
            .post(body).build()


        //　POST通信を行います
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //ネットワークにつながっていない際に呼ばれます

                //Activity（コントローラ）に通知
                listener.onFailure()

            }

            override fun onResponse(call: Call, response: Response) {
                //レスポンスが返却された際に呼ばれます

                //Activity（コントローラ）に通知
                listener.onSuccess(response.code.toString(), response.message, response.body!!.string())

            }
        })
    }
}