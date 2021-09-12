package com.example.facemusic.service

import android.content.Context
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
import com.example.facemusic.`interface`.S3UpLoadObjectListener
import com.example.facemusic.const.Constants
import java.io.File
import java.util.*


/** Amazon S3に画像をアップロードするクラスです **/

class S3Client : TransferListener {

    /** 変数 **/
    // S3へのアップロード後のリスナー
    private var listener: S3UpLoadObjectListener? = null

    /** 定数　**/
    // アクセスキーID
    private val ACCESS_KEY_ID = "AKIAT7N5KMTBE2HYX6T4"
    //　シークレットアクセスキー
    private val SECRET_ACCESS_KEY = "GcKWIdHPeFiga1X2W1/ajfYq5c6G2CKXr9Rt9L2L"
    //バケット名
    private val BUCKET_NAME = "face-music-app"
    // オブジェクトへの有効期限
    private val EXPIRATION_DURATION = 1000000


    /** シングルトン **/

    companion object {

        private val _instance: S3Client =
            S3Client()
        fun getInstance(): S3Client =
            _instance

    }


    /**
     * 画像をアップロードする関数です
     * @param context Context コンテキスト
     * @param file File 画像ファイル
     * @param listener S3UploadObjectListener インターフェイス
     */

    fun uploadImage(context: Context, file: File, fileName: String, listener: S3UpLoadObjectListener) {

        this.listener = listener

        val transferUtitlity: TransferUtility =
            TransferUtility.builder().s3Client(createS3Client()).context(context).build()

        val transferObserver: TransferObserver = transferUtitlity.upload(BUCKET_NAME, fileName, file)

        transferObserver.setTransferListener(this)

    }

    /** S3クライアントを作成する関数です **/

    private fun createS3Client(): AmazonS3 {

        // AWSの認証情報を作成します
        val credentials: AWSCredentials = BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY)

        // S3のクライアントを生成します
        val client: AmazonS3 = AmazonS3Client(credentials, Region.getRegion(Regions.AP_NORTHEAST_1))

        return client

    }


    /**
     * バケット内に指定のディレクトリが存在するかをチェックする関数です
     * @param key String オブジェクトキー
     */

    fun existDirectory(key: String): Boolean {

        // オブジェクトが存在しているかを判定するフラグです
        var exsitObject: Boolean = false

        try {
            // 指定のオブジェクトが存在するかどうかを判定します
            exsitObject = createS3Client().doesObjectExist(BUCKET_NAME, key)

        } catch (ase: AmazonServiceException) {
            // リクエスト処理中にエラーが発生した際に呼ばれます
            exsitObject = false

        } catch (ace: AmazonClientException) {
            //リクエストをする際、もしくはレスポンスを処理している場合に、クライアント側でエラーが発生した際に呼ばれます
            exsitObject = false
        }

        return exsitObject
    }


    /**
     * 指定した画像ファイルを削除する関数です
     * @param key String オブジェクトキー
     */

    fun deleteObject(key: String) {

        try {
            // 指定のオブジェクトを削除します
            val dor = DeleteObjectRequest(BUCKET_NAME, key)
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
     */

    fun createObjectUrl(key: String): String {

        //署名つきオブジェクトURL
        var presignedUrl: String = ""

        try {
            val expiration = Date()
            var expTimeMillis = expiration.time

            //有効期限を設定します
            expTimeMillis += EXPIRATION_DURATION
            expiration.time = expTimeMillis

            //指定のオブジェクトをGETする署名付きURLを設定を設定します
            val generatePresignedUrlRequest =
                GeneratePresignedUrlRequest(BUCKET_NAME, key).withMethod(HttpMethod.GET)
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
     */

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
            listener?.onS3Uploaded(Constants.UPLOADED_COMPLETE, null)

        } else if (state == TransferState.FAILED) {
            //送信が失敗した場合
            listener?.onS3Uploaded(Constants.UPLOADED_FAILED, null)

        }

    }

    /**
     * 画像アップロード時に例外が発生したときに呼ばれるコールバック関数です
     * @param id Int 送信した際の固有のID
     * @param ex Exception 例外クラス
     * */
    override fun onError(id: Int, ex: Exception?) {

        listener?.onS3Uploaded(Constants.UPLOADED_FAILED, ex)

    }

}