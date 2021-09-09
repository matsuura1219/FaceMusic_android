package com.example.facemusic.activity

//Kotlin Android Extensionsにより、xmlのコンポーネントの初期化をする必要なく、IDを変数として扱うことができます
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facemusic.R
import com.example.facemusic.`interface`.FaceApiListener
import com.example.facemusic.`interface`.S3UpLoadObjectListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Constants
import com.example.facemusic.json.faceapi.FaceApiData
import com.example.facemusic.service.FaceApiClient
import com.example.facemusic.service.S3Client
import com.example.facemusic.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.*
import java.lang.Exception

/** アプリ操作の説明画面およびカメラ撮影を行う画面です **/

class CameraActivity : Activity(), FaceApiListener, S3UpLoadObjectListener, View.OnClickListener {

    /** 変数 **/
    //Amazon S3にアップロードした画像のオブジェクトキー
    var fileName: String = ""

    /** 定数 **/
    // カメラ撮影後に返却されるステ―タスコード
    private val CAMERA_REQUEST_CODE = 2

    // カメラ権限のリクエストコード
    private val CAMERA_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // 初期化を行います
        init()

    }


    /** 初期化を行う関数です **/

    private fun init() {

        // イベント定義
        back.setOnClickListener(this)
        next.setOnClickListener(this)

        // 文言設定

        if (MainApplication.getInstance().getUserInfo().getSelectContent()
                .equals(Constants.EMOTION_DETECTION)
        ) {
            //[EmotionDetection]を選択した場合
            title_text.text = getString(R.string.emotion_detection)
            step2_title.text = getString(R.string.step2_title_emotion_detection)
            step2_message.text = getString(R.string.step2_explanation_emotion_detection)

        } else if (MainApplication.getInstance().getUserInfo().getSelectContent()
                .equals(Constants.AGE_DETECTION)
        ) {
            //[AgeDetection]を選択した場合
            title_text.text = getString(R.string.age_detection)
            step2_title.text = getString(R.string.step2_title_age_detection)
            step2_message.text = getString(R.string.step2_explanation_age_detection)

        }


    }

    /** パーミッションの有無を確認するための関数です **/

    private fun checkCameraPermission() {

        // 現在のパーミッションの許可状態を確認します
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // パーミッションが許可されていない場合
            // パーミッションをリクエストします
            makeRequest()

        } else {
            //パーミッションが許可されている場合

            //カメラを起動し、撮影を開始します
            takePicture()

        }
    }

    /** パーミッションをリクエストするための関数です **/

    private fun makeRequest() {
        // パーミッションを許可するためのダイアログを表示します
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    //パーミッションの許可の結果を取得するコールバック関数です
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // パーミッションが許可されている場合、PackageManager.PERMISSION_GRANTEDが格納されています
            // パーミッションが許可されていない場合、PackageManager.PERMISSION_DENIEDが格納されています
            if (!grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // カメラのパーミッションが許可されている場合

                // カメラを起動し、撮影を開始します
                takePicture()
            }
        }
    }

    /**
     * 撮影を開始するメソッドです
     * */
    private fun takePicture() {

        // 暗黙的インテントを使用します
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    /** カメラ撮影後に実行されるコールバック関数です
     * @param requestCode Int リクエストコード
     * @param resultCode Int 結果のステータスコード
     * @param data Intent? 撮影後のデータ
     * */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //　リクエストコードがtakePicture()で設定したリクエストコード かつ 撮影が正常に終了したとき

            //オーバーレイ表示をします
            overlay.visibility = View.VISIBLE

            //撮影した画像をBitmap形式で保存します
            val bitmap = data?.extras?.get("data").let {

                // letはスコープ関数の1つで、なんらかの変数に対する処理を拡張関数としてまとめて記述する仕組みです。
                // この場合、[data?.extras?.get("data")がnullでない場合、変数itをカッコ内で処理します

                // 撮影画像をBitmap型に変換します
                val img = it as Bitmap

                // 共通領域に設定します
                MainApplication.getInstance().getUserInfo().setPhoto(img)

                // オブジェクトキー
                fileName = CommonUtil.getRandomNumber()

                // Amazon S3 に画像を保存します
                // [!!]は強制アンラップを行います
                S3Client.getInstance().uploadImage(
                    this,
                    CommonUtil.createUploadFile(this, img, "face-img")!!,
                    fileName,
                    this
                )

            }
        }
    }


    /** クリックイベント後に実行されるコールバック関数です
     * @param p0 View? フォーカスが当たっているViewコンポーネント
     */

    override fun onClick(p0: View?) {

        when (p0!!.id) {

            back.id -> {

                finish()
            }

            next.id -> {

                // カメラのアプリが端末に存在するかを確認します
                if (Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager) != null) {
                    // カメラアプリが端末に存在する場合

                    // パーミッションを得ているかを確認します
                    checkCameraPermission()

                } else {
                    // カメラアプリが端末に存在しない場合

                    DialogUtil.getInstance().showMessageToSettings(
                        getString(R.string.camera_no_permission),
                        getString(R.string.yes),
                        this
                    )

                }

            }

        }
    }


    /**
     * Amazon S3に画像アップロード後に呼ばれるコールバック関数です
     * @param state String アップロードが正常に終了したかどうか（1: OK, 0: NG）
     * @param ex Exception 例外情報
     */

    override fun onS3Uploaded(resultCode: String, ex: Exception?) {

        if (resultCode.equals(Constants.UPLOADED_COMPLETE)) {
            //S3へ画像アップロードに成功した場合

            //コルーチンを生成します（バックグランドで処理を行います）
            val cor = CoroutineScope(Dispatchers.Default)

            //コルーチンを立ち上げます
            cor.launch {

                // URLを生成します
                var url = S3Client.getInstance().createObjectUrl(fileName)
                // FaceAPIをコールします
                FaceApiClient.getInstance().sendImageToMicroSoft(url, this@CameraActivity)
            }

        } else {

            // S3へ画像アップロードに失敗した場合
            // ポップアップを表示させます。
            val coroutine = CoroutineScope(Dispatchers.Main)
            coroutine.launch {
                DialogUtil.getInstance().showErrorMessage(
                    getString(R.string.s3_not_uploaded), getString(
                        R.string.yes
                    ), applicationContext
                )
            }

        }
    }


    /**
     * FaceAPIをコールした後、レスポンスが返却されたときに呼ばれるコールバック関数です
     * @param errorCode String ステータスコード
     * @param data String? Jsonオブジェクト（顔情報）
     */

    override fun onSuccess(statusCode: String, message: String, data: String?) {

        if (statusCode.equals(Constants.STATUS_CODE_NORMAL) && message.equals(Constants.SUCCESS_FACE_API)) {

            // 正常にレスポンスデータが返却された場合
            //アップロードした画像を削除します
            S3Client.getInstance().deleteObject(fileName)

            // jsonデータをパースします
            val mapper = jacksonObjectMapper()
            val jsonData = mapper.readValue<ArrayList<FaceApiData>>(data!!)


            if (jsonData.size == 0) {
                // 顔画像が検出されなかった場合

                // メインスレッドで描画処理を行います
                val coroutine = CoroutineScope(Dispatchers.Main)
                coroutine.launch {

                    // オーバーレイを解除します
                    overlay.visibility = View.INVISIBLE

                    DialogUtil.getInstance().showErrorMessage(
                        getString(R.string.faceapi_non_people),
                        getString(R.string.yes),
                        applicationContext
                    )
                }

                return
            }

            if (jsonData.size > 1) {
                // 2名以上の顔画像が存在する場合
                // メインスレッドで描画処理を行います
                val coroutine = CoroutineScope(Dispatchers.Main)
                coroutine.launch {
                    DialogUtil.getInstance().showErrorMessage(
                        getString(R.string.faceapi_many_people),
                        getString(R.string.yes),
                        applicationContext
                    )
                }
                return
            }

            // 共通領域に設定します
            MainApplication.getInstance().getUserInfo().setFaceApiData(jsonData.get(0))

            // メインスレッドで描画処理を行います
            val coroutine = CoroutineScope(Dispatchers.Main)

            coroutine.launch {

                // オーバーレイを解除します
                overlay.visibility = View.INVISIBLE

                if (MainApplication.getInstance().getUserInfo().getSelectContent()
                        .equals(Constants.EMOTION_DETECTION)
                ) {
                    // [FaceDetection]を選択した場合

                    //画面遷移を行います
                    val intent =
                        Intent(this@CameraActivity, ShowResultForEmotionDetection::class.java)
                    startActivity(intent)

                } else if (MainApplication.getInstance().getUserInfo().getSelectContent()
                        .equals(Constants.AGE_DETECTION)
                ) {
                    // [AgeDetection]を選択した場合

                    //画面遷移を行います
                    /*
                    val intent = Intent(this@CameraActivity, ShowResultForAgeDetection::class.java)
                    startActivity(intent)
                    */

                }

            }

        } else {

            // FaceAPIのコールに失敗した場合

            // ポップアップ画面を表示します
            // メインスレッドで描画処理を行います
            val coroutine = CoroutineScope(Dispatchers.Main)
            coroutine.launch {
                DialogUtil.getInstance().showMessageToSettings(
                    getString(R.string.faceapi_not_work), getString(
                        R.string.yes
                    ), applicationContext
                )
            }

        }

    }

    /** FaceAPIをコールした後に、レスポンスが返却されないとき（ネットワークに接続されていない場合）に呼ばれるコールバック関数です **/

    override fun onFailure() {

        // ポップアップ画面を表示します
        // メインスレッドで描画処理を行います
        val coroutine = CoroutineScope(Dispatchers.Main)
        coroutine.launch {
            DialogUtil.getInstance().showErrorMessage(
                getString(R.string.not_connect_network), getString(
                    R.string.yes
                ), applicationContext
            )
        }

    }

}
