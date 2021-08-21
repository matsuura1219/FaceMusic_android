package com.example.facemusic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import com.example.facemusic.`interface`.SpotifyAuthListener
import com.example.facemusic.const.Exconst
import com.example.facemusic.util.DialogUtil
import com.example.facemusic.util.SpotifyApiUtil
//Kotlin Android Extensionsにより、xmlのコンポーネントの初期化をする必要なく、IDを変数として扱うことができます
import kotlinx.android.synthetic.main.activity_main.*

/** オープニング画面です **/

class MainActivity : Activity(), SpotifyAuthListener, DialogUtil.OnClickButton  {

    /** 定数 **/
    //ハンドラー（UI処理をサブスレッドで実行するためのクラスです）
    private val mHandler = Handler(Looper.getMainLooper())
    //起動からロゴのフェードインにかかる時間
    private val fadeInAnimationTime: Long = 1000
    //起動から画面遷移にかかる時間
    private val ANIMATION_TIME: Long = 2000

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
        //アニメーションを実行します
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

            //Spotifyアプリと接続をします
            SpotifyApiUtil.getInstance().connectToSpotifyApp(this, this)

        }, ANIMATION_TIME)

    }

    /** Spotifyとの認証が完了した時に実行されるコールバック関数です */
    override fun onAuthenticationResponse(resultCode: String) {

        if (resultCode.equals(Exconst.AUTHENTICATION_COMPLETE)) {
            //認証に成功したとき

            //画面遷移を行います
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

        } else {

            if (resultCode.equals(Exconst.COULD_NOT_FIND_APP)) {

                //アプリが未インストールの場合
                DialogUtil.getInstance().showAlertMessageToGooglePlay("Spotifyアプリがインストールされていないため、本アプリを使用できません。Google Playにて、アプリのインストールをお願いします。", "はい", this, this)

            } else {

                //認証に失敗したとき
                DialogUtil.getInstance().showCloseMessage("認証に失敗しました。アプリを終了します。", "はい", this, this)

            }

        }

    }

    /** ポップアップ画面が表示され、ボタンを押下した際に実行されるコールバック関数です */
    override fun onClickPositiveButton() {


    }

}