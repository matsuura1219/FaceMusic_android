package com.example.facemusic.util

import android.R.attr.path
import android.content.Context
import android.util.Log
import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.example.facemusic.`interface`.UpLoadObjectListener
import com.example.facemusic.const.Exconst
import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import java.util.*


/** Amazon S3に画像をアップロードするクラスです **/

class UploadObject : TransferListener {

    /** 変数 **/
    private var _listener: UpLoadObjectListener? = null

    /** 定数（Face AWS） **/

    //アクセスキーID
    private val accessKeyId = "AKIAT7N5KMTBE2HYX6T4"

    //シークレットアクセスキー
    private val secretAccessKey = "GcKWIdHPeFiga1X2W1/ajfYq5c6G2CKXr9Rt9L2L"

    //バケット名
    private val bucketName = "face-music-app"


    //static領域（シングルトン）
    companion object {

        private val _instance: UploadObject = UploadObject()
        fun getInstance(): UploadObject = _instance

    }


    /**
     * 画像をアップロードする関数です
     * @param context Context コンテキスト
     * @param file File 画像ファイル
     * @param listener UploadObjectListener インターフェイス
     * */
    fun uploadImage(
        context: Context,
        file: File,
        fileName: String,
        listener: UpLoadObjectListener
    ) {

        _listener = listener

        val transferUtitlity: TransferUtility =
            TransferUtility.builder().s3Client(createS3Client()).context(context).build()

        val transferObserver: TransferObserver = transferUtitlity.upload(bucketName, fileName, file)

        transferObserver.setTransferListener(this)

    }

    /**
     * S3クライアントを作成する関数です
     * */
    private fun createS3Client(): AmazonS3 {

        //AWSの認証情報を作成します
        val credentials: AWSCredentials = BasicAWSCredentials(accessKeyId, secretAccessKey)

        //S3のクライアントを生成します
        val client: AmazonS3 = AmazonS3Client(credentials, Region.getRegion(Regions.AP_NORTHEAST_1))

        return client

    }


    /**
     * バケット内に指定のディレクトリが存在するかをチェックする関数です
     * @param key String オブジェクトキー
     * */
    fun existDirectory(key: String): Boolean {

        var flag: Boolean = false

        try {
            flag = createS3Client().doesObjectExist(bucketName, key)
        } catch (ase: AmazonServiceException) {
            //リクエスト処理中にエラーが発生した際に呼ばれます
            flag = false
        } catch (ace: AmazonClientException) {
            //リクエストをする際、もしくはレスポンスを処理している場合に、クライアント側でエラーが発生した際に呼ばれます
            flag = false
        }

        return flag
    }


    /**
     * 指定した画像ファイルを削除する関数です
     * @param key String オブジェクトキー
     * */
    fun deleteObject(key: String) {

        try {
            val dor = DeleteObjectRequest(bucketName, key)
            createS3Client().deleteObject(dor)
        } catch (ase: AmazonServiceException) {
            //リクエスト処理中にエラーが発生した際に呼ばれます
        } catch (ace: AmazonClientException) {
            //リクエストをする際、もしくはレスポンスを処理している場合に、クライアント側でエラーが発生した際に呼ばれます
        }

    }


    /**
     * 署名付きオブジェクトURLを生成する関数です
     * @param key String オブジェクトキー
     * */
    fun createObjectUrl(key: String): String {

        //署名つきオブジェクトURL
        var presignedUrl: String = ""

        try {
            val expiration = Date()
            var expTimeMillis = expiration.time

            //有効期限を設定します
            expTimeMillis += 1000 * 60 * 60
            expiration.time = expTimeMillis

            //指定のオブジェクトをGETする署名付きURLを設定を設定します
            val generatePresignedUrlRequest =
                GeneratePresignedUrlRequest(bucketName, key).withMethod(HttpMethod.GET)
                    .withExpiration(expiration)


            val url = createS3Client().generatePresignedUrl(generatePresignedUrlRequest)

            //文字列に変換します
            presignedUrl = url.toString()

        } catch (ase: AmazonServiceException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.

        }

        return presignedUrl


    }

    /**
     * バイト(ファイル)が送信される度に呼ばれるコールバック関数です
     * @param id Int 送信した際の固有のID
     * @param bytesCurrent Long 現在送信されたバイト数
     * @param bytesTotal Long 送信されるべき合計のバイト数
     * */
    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        //特にコーディングする必要なし
    }

    /**
     * 送信の際の状態が変化したときに呼ばれるコールバック関数です
     * @param id Int 送信した際の固有のID
     * @param state TransferState 送信状態
     * */
    override fun onStateChanged(id: Int, state: TransferState?) {

        if (state == TransferState.COMPLETED) {
            //送信が完了した場合
            _listener?.onUploaded(Exconst.UPLOADED_COMPLETE, null)

        } else if (state == TransferState.FAILED) {
            //送信が失敗した場合
            _listener?.onUploaded(Exconst.UPLOADED_FAILED, null)

        }

    }

    /**
     * 画像アップロード時に例外が発生したときに呼ばれるコールバック関数です
     * @param id Int 送信した際の固有のID
     * @param ex Exception 例外クラス
     * */
    override fun onError(id: Int, ex: Exception?) {

        _listener?.onUploaded(Exconst.UPLOADED_FAILED, ex)

    }


}