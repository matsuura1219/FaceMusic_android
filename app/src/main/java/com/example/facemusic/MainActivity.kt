package com.example.facemusic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
//Kotlin Android Extensionsにより、xmlのコンポーネントの初期化をする必要なく、IDを変数として扱うことができます
import kotlinx.android.synthetic.main.activity_main.*

/** オープニング画面です **/

class MainActivity : Activity() {

    /** 定数 **/
    //ハンドラー（UI処理をサブスレッドで実行するためのクラスです）
    private val mHandler = Handler(Looper.getMainLooper())
    //起動からロゴのフェードインにかかる時間
    private val fadeInAnimationTime: Long = 1000
    //起動から画面遷移にかかる時間
    private val animationTime: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //アニメーションを実行します
        doAnimation()
        //アプリ起動時にアニメーションを実行します
        showOpening()
    }

    /** アニメーションを実行する関数です **/
    private fun doAnimation () {
        //imageViewのアルファ―値を0.0fから1.0fへ変化させるフェードインアニメーション
        val fadeAnim = AlphaAnimation(0.0f, 1.0f)
        //フェードインにかかる時間を設定
        fadeAnim.duration = fadeInAnimationTime
        //アルファー値をアニメーション終了後の値を維持するように設定
        fadeAnim.fillAfter = true
        logo.animation = fadeAnim
    }

    override fun onPause() {
        super.onPause()
        //画面遷移後、この画面を破棄します
        finish()
    }

    /** アプリ起動時のアニメーションを実行する関数です **/
    private fun showOpening () {

        mHandler.postDelayed(Runnable {

            //一定時間が経った後、画面遷移を行います

            val intent = Intent(this, LoginActivity::class.java)
            //val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)

        }, animationTime)

    }

}