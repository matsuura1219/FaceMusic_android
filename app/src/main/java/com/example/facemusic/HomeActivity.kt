package com.example.facemusic

//Kotlin Android Extensionsにより、xmlのコンポーネントの初期化をする必要なく、IDを変数として扱うことができます
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Exconst
import com.example.facemusic.util.EC2ServerComm
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_home.*


/** ホーム画面です **/

class HomeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var a = SpotifyApiUtil.getInstance().getMusicInfo()

        //撮影画像から表情を取得し、最適な楽曲を提供するcardViewのクリックイベントを設定します
        emotion_detection.setOnClickListener {

            //共通領域に設定します
            MainApplication.getInstance().setSelectContent(Exconst.EMOTION_DETECTION)

            //画面遷移を行います
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)

        }

        //撮影画像から年齢を取得し、最適な楽曲を提供するcardViewのクリックイベントを設定します
        age_detection.setOnClickListener {

            //共通領域に設定します
            MainApplication.getInstance().setSelectContent(Exconst.FACE_DETECTION)

            //画面遷移を行います
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)

        }

    }

}