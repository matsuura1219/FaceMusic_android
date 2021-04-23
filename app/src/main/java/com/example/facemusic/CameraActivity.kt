package com.example.facemusic

//Kotlin Android Extensionsにより、xmlのコンポーネントの初期化をする必要なく、IDを変数として扱うことができます
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facemusic.`interface`.FaceApiListener
import com.example.facemusic.`interface`.UpLoadObjectListener
import com.example.facemusic.const.Exconst
import com.example.facemusic.util.AppServerCom
import com.example.facemusic.util.CommonUtil
import com.example.facemusic.util.UploadObject
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

                //オブジェクトキー
                fileName = CommonUtil.getRandomNumber()

                //Amazon S3 に画像を保存します
                //[!!]は強制アンラップを行います
                UploadObject.getInstance().uploadImage(this, CommonUtil.createUploadFile(this, img, "face-img")!!, fileName,this)


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
        UploadObject.getInstance().deleteObject(fileName)

        //メインスレッドで描画処理を行います
        val coroutine = CoroutineScope(Dispatchers.Main)

        coroutine.launch {

            //オーバーレイを解除します
            overlay.visibility = View.INVISIBLE

        }

        if (errorCode.equals(Exconst.STATUS_CODE_NORMAL)) {

            Log.d("develop", "FaceAPIを無事に呼び出すことができました")
            Log.d("develop", "dataは"+data)

            val json_data = JSONArray(data)
            val json = json_data.getJSONObject(0)

            val faceRectangle: JSONObject = json.getJSONObject("faceRectangle")
            val top: Int = faceRectangle.getInt("top")
            Log.d("develop", "topは"+top)
            val left: Int = faceRectangle.getInt("left")
            Log.d("develop", "leftは"+left)
            val width: Int = faceRectangle.getInt("width")
            Log.d("develop", "widthは"+width)
            val height: Int = faceRectangle.getInt("height")
            Log.d("develop", "heightは"+height)

            val faceAttributes: JSONObject = json.getJSONObject("faceAttributes")

            /*
            val smile: Double = faceAttributes.getDouble("smile")
            Log.d("develop", "smileは"+smile)
            val headPose: JSONObject = faceAttributes.getJSONObject("headPose")
            val pitch: Double = headPose.getDouble("pitch")
            Log.d("develop", "pitchは"+pitch)
            val roll: Double = headPose.getDouble("roll")
            Log.d("develop", "rollは"+roll)
            val yaw: Double = headPose.getDouble("yaw")
            Log.d("develop", "yawは"+yaw)

            val gender = faceAttributes.getString("gender")
            Log.d("develop", "genderは"+gender)
            val age: Double = faceAttributes.getDouble("age")
            Log.d("develop", "ageは"+age)

            val facialHair: JSONObject = faceAttributes.getJSONObject("facialHair")
            val moustache: Double = facialHair.getDouble("moustache")
            Log.d("develop", "moustcheは"+moustache)
            val beard: Double = facialHair.getDouble("beard")
            Log.d("develop", "beardは"+beard)
            val sideburns: Double = facialHair.getDouble("sideburns")
            Log.d("develop", "sideburnsは"+sideburns)

            val glasses: String = faceAttributes.getString("glasses")
            Log.d("develop", "glassesは"+glasses)

            */

            /*
            val emotion: JSONObject = faceAttributes.getJSONObject("emotion")
            val anger: Double = emotion.getDouble("anger")
            Log.d("develop", "angerは"+anger)
            val contempt: Double = emotion.getDouble("contempt")
            Log.d("develop", "comtemptは"+contempt)
            val disgust: Double = emotion.getDouble("disgust")
            Log.d("develop", "disgustは"+disgust)
            val fear: Double = emotion.getDouble("fear")
            Log.d("develop", "fearは"+fear)
            val happiness: Double = emotion.getDouble("happiness")
            Log.d("develop", "hapinessは"+happiness)
            val neutral: Double = emotion.getDouble("neutral")
            Log.d("develop", "neutralは"+neutral)
            val sadness: Double = emotion.getDouble("sadness")
            Log.d("develop", "sadnessは"+sadness)
            val surprise: Double = emotion.getDouble("surprise")
            Log.d("develop", "surpriseは"+surprise)

             */

            val emotion: JSONObject = faceAttributes.getJSONObject("emotion")
            val anger: Double = emotion.getDouble("anger")
            Log.d("develop", "angerは"+anger)
            val contempt: Double = emotion.getDouble("contempt")
            Log.d("develop", "comtemptは"+contempt)
            val disgust: Double = emotion.getDouble("disgust")
            Log.d("develop", "disgustは"+disgust)
            val fear: Double = emotion.getDouble("fear")
            Log.d("develop", "fearは"+fear)
            val happiness: Double = emotion.getDouble("happiness")
            Log.d("develop", "hapinessは"+happiness)
            val neutral: Double = emotion.getDouble("neutral")
            Log.d("develop", "neutralは"+neutral)
            val sadness: Double = emotion.getDouble("sadness")
            Log.d("develop", "sadnessは"+sadness)
            val surprise: Double = emotion.getDouble("surprise")
            Log.d("develop", "surpriseは"+surprise)


            /*
            val blur: JSONObject = faceAttributes.getJSONObject("blur")
            val blurLevel: String = blur.getString("blurLevel")
            Log.d("develop", "blurLevelは"+blurLevel)
            val blur_value: Double = blur.getDouble("value")
            Log.d("develop", "blur_valueは"+blur_value)

            val exposure: JSONObject = faceAttributes.getJSONObject("exposure")
            val exposureLevel: String = exposure.getString("exposureLevel")
            Log.d("develop", "exposureLevelは"+exposureLevel)
            val exposure_value: Double = exposure.getDouble("value")
            Log.d("develop", "exposure_valueは"+exposure_value)

            val noise: JSONObject = faceAttributes.getJSONObject("noise")
            val noiseLevel: String = noise.getString("noiseLevel")
            Log.d("develop", "noiseLevelは"+noiseLevel)
            val noise_value: Double = noise.getDouble("value")
            Log.d("develop", "noise_valueは"+noise_value)

            val makeup: JSONObject = faceAttributes.getJSONObject("makeup")
            val eyeMakeup: Boolean = makeup.getBoolean("eyeMakeup")
            Log.d("develop", "eyeMakeupは"+eyeMakeup)
            val lipMakeup: Boolean = makeup.getBoolean("lipMakeup")
            Log.d("develop", "lipMakeupは"+lipMakeup)

            val accesories: JSONArray = faceAttributes.getJSONArray("accessories")

            val occlusion: JSONObject = faceAttributes.getJSONObject("occlusion")
            val foreheadOccluded: Boolean = occlusion.getBoolean("foreheadOccluded")
            Log.d("develop", "foreheadOccludedは"+foreheadOccluded)
            val eyeOccluded = occlusion.getBoolean("eyeOccluded")
            Log.d("develop", "eyeOccludedは"+eyeOccluded)
            val mouthOccluded = occlusion.getBoolean("mouthOccluded")
            Log.d("develop", "mouthOccludedは"+mouthOccluded)

            val hair: JSONObject = faceAttributes.getJSONObject("hair")
            val bald: Double = hair.getDouble("bald")
            Log.d("develop", "baldは"+bald)
            val invisible: Boolean = hair.getBoolean("invisible")
            Log.d("develop", "invisibleは"+invisible)
            val hairColor: JSONArray = hair.getJSONArray("hairColor")

            val black = hairColor.getJSONObject(0)
            val black_color: String = black.getString("color")
            Log.d("develop", "black_colorは"+black_color)
            val black_confidence: Double = black.getDouble("confidence")
            Log.d("develop", "black_confidenceは"+black_confidence)

            val brown = hairColor.getJSONObject(1)
            val brown_color: String = brown.getString("color")
            Log.d("develop", "brown_colorは"+brown_color)
            val brown_confidence: Double = brown.getDouble("confidence")
            Log.d("develop", "brown_confidenceは"+brown_confidence)

            val gray = hairColor.getJSONObject(2)
            val gray_color: String = gray.getString("color")
            Log.d("develop", "gray_colorは"+gray_color)
            val gray_confidence: Double = gray.getDouble("confidence")
            Log.d("develop", "gray_confidenceは"+gray_confidence)

            val other = hairColor.getJSONObject(3)
            val other_color: String = other.getString("color")
            Log.d("develop", "other_colorは"+other_color)
            val other_confidence: Double = other.getDouble("confidence")
            Log.d("develop", "other_confidenceは"+other_confidence)

            val blond = hairColor.getJSONObject(4)
            val blond_color: String = blond.getString("color")
            Log.d("develop", "blond_colorは"+blond_color)
            val blond_confidence: Double = blond.getDouble("confidence")
            Log.d("develop", "blond_confidenceは"+blond_confidence)

            val red= hairColor.getJSONObject(5)
            val red_color: String = red.getString("color")
            Log.d("develop", "red_colorは"+red_color)
            val red_confidence: Double = red.getDouble("confidence")
            Log.d("develop", "red_confidenceは"+red_confidence)

            val white = hairColor.getJSONObject(6)
            val white_color: String = white.getString("color")
            Log.d("develop", "white_colorは"+white_color)
            val white_confidence: Double = white.getDouble("confidence")
            Log.d("develop", "white_confidenceは"+white_confidence)

            */

            //MainスレッドでUIを更新します
            val cor = CoroutineScope(Dispatchers.Main)

            cor.launch {

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
                var url = UploadObject.getInstance().createObjectUrl(fileName)
                //FaceAPIをコールします
                AppServerCom.getInstance().sendImageToMicroSoft(url,this@CameraActivity)
            }

        } else {
            //S3へ画像アップロードに失敗した場合

        }
    }


}