package com.example.facemusic

//Kotlin Android Extensionsにより、xmlのコンポーネントの初期化をする必要なく、IDを変数として扱うことができます
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facemusic.`interface`.FaceApiListener
import com.example.facemusic.`interface`.UpLoadObjectListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Exconst
import com.example.facemusic.util.FaceApiUtil
import com.example.facemusic.util.CommonUtil
import com.example.facemusic.util.S3Util
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

/** アプリ操作の説明画面です **/

class CameraActivity : Activity(), FaceApiListener, UpLoadObjectListener {

    /** 変数 **/
    //Amazon S3にアップロードした画像のオブジェクトキー
    var fileName: String = ""

    /** 定数 **/
    private val CAMERA_REQUEST_CODE = 2
    //リクエストコードは任意で設定
    private val CAMERA_PERMISSION_REQUEST_CODE = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        //戻るアイコンを押下したときのイベントを設定します
        back.setOnClickListener {
            //現在のActivityを閉じ、前画面に戻ります
            finish()
        }

        //STARTを押下したときのイベントを設定します
        next.setOnClickListener {
            //カメラを起動します

            //カメラのアプリが端末に存在するかを確認します
            if (Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager) != null) {
                //カメラアプリが端末に存在する場合

                //パーミッションを得ているかを確認します
                checkCameraPermission()

            } else {
                //カメラアプリが端末に存在しない場合

            }

        }

        if (MainApplication.getInstance().getSelectContent() == 0) {

            title_text.text = "EmotionDetect"
            step2_title.text = "感情の推論"
            step2_message.text = "AIを活用し、撮影画像から感情を取得します。"



        } else if (MainApplication.getInstance().getSelectContent() == 1) {

            title_text.text = "AgeDetect"
            step2_title.text = "年齢の推論"
            step2_message.text = "AIを活用し、撮影画像から年齢を取得します。"

        }
    }

    //パーミッションの有無を確認するための関数です
    private fun checkCameraPermission () {

        //現在のパーミッションの許可状態を確認します
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //パーミッションが許可されていない場合
            //パーミッションをリクエストします
            makeRequest()

        } else {
            //パーミッションが許可されている場合

            //カメラを起動し、撮影を開始します
            takePicture()

        }
    }

    //パーミッションをリクエストするための関数です
    private fun makeRequest () {
        //パーミッションを許可するためのダイアログを表示します
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    //パーミッションの許可の結果を取得するコールバック関数です
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            //パーミッションが許可されている場合、PackageManager.PERMISSION_GRANTEDが格納されています
            //パーミッションが許可されていない場合、PackageManager.PERMISSION_DENIEDが格納されています
            if (!grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //カメラのパーミッションが許可されている場合

                //カメラを起動し、撮影を開始します
                takePicture()
            }
        }
    }

    /**
     * 撮影を開始するメソッドです
     * */
    private fun takePicture () {

        //暗黙的インテントを使用します
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    //カメラ撮影後に実行されるコールバック関数です
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //リクエストコードがtakePicture()で設定したリクエストコード かつ 撮影が正常に終了したとき

            //オーバーレイ表示をします
            overlay.visibility = View.VISIBLE

            //撮影した画像をBitmap形式で保存します
            val bitmap = data?.extras?.get("data").let {

                //letはスコープ関数の1つで、なんらかの変数に対すおる処理を拡張関数としてまとめて記述する仕組みです。
                //この場合、[data?.extras?.get("data")がnull出ない場合、変数itをカッコ内で処理します

                //撮影画像をBitmap型に変換します
                val img = it as Bitmap

                //共通領域に設定します
                MainApplication.getInstance().setPhoto(img)

                //オブジェクトキー
                fileName = CommonUtil.getRandomNumber()

                //Amazon S3 に画像を保存します
                //[!!]は強制アンラップを行います
                S3Util.getInstance().uploadImage(this, CommonUtil.createUploadFile(this, img, "face-img")!!, fileName,this)


            }

        }
    }


    /**
     * FaceAPIをコールした後、レスポンスが返却されたときに呼ばれるコールバック関数です
     * @param errorCode String ステータスコード
     * @param data String? Jsonオブジェクト（顔情報）
     * */
    override fun onSuccess(errorCode: String, data: String?) {

        //アップロードした画像を削除します
        S3Util.getInstance().deleteObject(fileName)

        //メインスレッドで描画処理を行います
        val coroutine = CoroutineScope(Dispatchers.Main)

        coroutine.launch {

            //オーバーレイを解除します
            overlay.visibility = View.INVISIBLE

        }

        if (errorCode.equals(Exconst.STATUS_CODE_NORMAL)) {

            val json_data = JSONArray(data)
            val json = json_data.getJSONObject(0)

            val faceRectangle: JSONObject = json.getJSONObject("faceRectangle")
            val top: Int = faceRectangle.getInt("top")
            val left: Int = faceRectangle.getInt("left")
            val width: Int = faceRectangle.getInt("width")
            val height: Int = faceRectangle.getInt("height")



            val faceAttributes: JSONObject = json.getJSONObject("faceAttributes")

            val age: Double = faceAttributes.getDouble("age")
            val gender: String = faceAttributes.getString("gender")

            val emotion: JSONObject = faceAttributes.getJSONObject("emotion")
            val anger: Double = emotion.getDouble("anger")
            val contempt: Double = emotion.getDouble("contempt")
            val disgust: Double = emotion.getDouble("disgust")
            val fear: Double = emotion.getDouble("fear")
            val happiness: Double = emotion.getDouble("happiness")
            val neutral: Double = emotion.getDouble("neutral")
            val sadness: Double = emotion.getDouble("sadness")
            val surprise: Double = emotion.getDouble("surprise")

            //MainスレッドでUIを更新します
            val cor = CoroutineScope(Dispatchers.Main)

            cor.launch {

                if (MainApplication.getInstance().getSelectContent() == 0) {

                    //画面遷移を行います
                    val intent = Intent(this@CameraActivity, ShowResultForFaceApiActivity::class.java)

                    //次画面へデータを送信します
                    intent.putExtra("anger", anger.toFloat())
                    intent.putExtra("contempt", contempt.toFloat())
                    intent.putExtra("disgust", disgust.toFloat())
                    intent.putExtra("fear", fear.toFloat())
                    intent.putExtra("happiness", happiness.toFloat())
                    intent.putExtra("neutral", neutral.toFloat())
                    intent.putExtra("sadness", sadness.toFloat())
                    intent.putExtra("surprise", surprise.toFloat())

                    startActivity(intent)

                } else if (MainApplication.getInstance().getSelectContent() == 1) {

                    //画面遷移を行います
                    val intent = Intent(this@CameraActivity, ShowResultForAge::class.java)

                    //次画面へデータを送信します
                    intent.putExtra("age", age.toFloat())
                    intent.putExtra("gender", gender)
                    intent.putExtra("top", top)
                    intent.putExtra("left", left)
                    intent.putExtra("width", width)
                    intent.putExtra("height", height)

                    startActivity(intent)
                }

            }


        } else {

            Log.d("develop", "FaceAPIを呼び出すことができませんでした")

        }
    }


    /**
     * FaceAPIをコールした後に、レスポンスが返却されないとき（ネットワークに接続されていない場合）に呼ばれるコールバック関数です
     * */
    override fun onFailure() {

        Log.d("develop", "ネットワークにつながっていません")

    }


    /**
     * Amazon S3に画像アップロード後に呼ばれるコールバック関数です
     * @param state String アップロードが正常に終了したかどうか（1: OK, 0: NG）
     * @param ex Exception 例外情報
     * */
    override fun onUploaded(state: String, ex: Exception?) {

        Log.d("develop", state)

        if (state.equals(Exconst.UPLOADED_COMPLETE)) {
            //S3へ画像アップロードに成功した場合

            //コルーチンを生成します（バックグランドで処理を行います）
            val cor = CoroutineScope(Dispatchers.Default)

            //コルーチンを立ち上げます
            cor.launch {

                //URLを生成します
                var url = S3Util.getInstance().createObjectUrl(fileName)
                //FaceAPIをコールします
                FaceApiUtil.getInstance().sendImageToMicroSoft(url,this@CameraActivity)
            }

        } else {
            //S3へ画像アップロードに失敗した場合

        }
    }


}