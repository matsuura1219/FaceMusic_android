package com.example.facemusic.activity

//Kotlin Android Extensionsにより、xmlのコンポーネントの初期化をする必要なく、IDを変数として扱うことができます
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.facemusic.R
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Constants
import com.example.facemusic.service.SpotifyApiClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*


/** ホーム画面です **/

class HomeActivity : Activity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 初期化を行います
        init()

    }

    /** 初期化処理を行う関数です **/

    private fun init () {

        // イベントを設定します
        emotionDetection.setOnClickListener(this)
        ageDetection.setOnClickListener(this)

    }

    /** クリックイベント後に実行されるコールバック関数です
     * @param p0 View? フォーカスが当たっているViewコンポーネント
     */

    override fun onClick(p0: View?) {

        when (p0!!.id) {

            emotionDetection.id -> {
                // [EmotionDetection]をクリックした場合
                // 共通領域に設定します
                MainApplication.getInstance().getUserInfo().setSelectContent(Constants.EMOTION_DETECTION)

            }

            ageDetection.id -> {
                // [AgeDetection]をクリックした場合
                // 共通領域に設定します
                MainApplication.getInstance().getUserInfo().setSelectContent(Constants.AGE_DETECTION)

            }

        }

        //画面遷移を行います
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)

    }

    override fun onDestroy() {
        super.onDestroy()

        if (MainApplication.getInstance().getUserInfo().getIsPlaying()) {

            // アプリ終了時に曲は停止します
            SpotifyApiClient.getInstance().stopMusic()

        }

    }

}