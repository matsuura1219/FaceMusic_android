package com.example.facemusic.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.example.facemusic.R
import com.example.facemusic.activity.CameraActivity
import kotlinx.coroutines.CoroutineScope


/** ポップアップ画面を表示するクラスです */

class DialogUtil {


    /** 定数 **/
    // Google Play上でのSpotofyアプリのインストール画面のURL
    private val SPOTIFY_URL: String = "market://details?id=com.spotify.music&hl=ja&gl=US"


    //static領域
    companion object {

        private val _instance: DialogUtil = DialogUtil()
        fun getInstance() = _instance

    }

    /** Spotifyアプリがインストールされず、認証が失敗した場合に、ダイアログを表示させた後、Google Playへ遷移させる関数です
     * @param message String 表示するメッセージ
     * @param buttonMessage String ボタン上のテキスト
     * @param context Context コンテキスト
     */

    fun showMessageToGooglePlay(message: String, buttonMessage: String, context: Context) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message)
            .setPositiveButton(buttonMessage, DialogInterface.OnClickListener { dialog, id ->
                // [はい]ボタンを押下した際の処理

                try {

                    // Google PlayのSpotifyへ戦死します
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SPOTIFY_URL)))

                } catch (error: android.content.ActivityNotFoundException) {

                    // Play Storeアプリを無効にしている場合
                    // アプリを終了します
                    showCloseMessage(context.getString(R.string.google_play_guard), "はい", context)
                }

            })

        // ポップアップ画面を表示します
        builder.show()

    }


    /** ダイアログを表示させた後、アプリケーションを終了する関数です
     * @param message String メッセージ
     * @param positiveButtonMessage String ボタン上のテキスト
     * @param context Context コンテキスト
     */

    fun showCloseMessage(message: String, positiveButtonMessage: String, context: Context) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message)
            .setPositiveButton(positiveButtonMessage, DialogInterface.OnClickListener { dialog, id ->
                // [はい]ボタンを押下した際の処理
                // アプリを終了します
                CommonUtil.finishApp(context as Activity)
            })

        // ポップアップ画面を表示します
        builder.show()

    }

    /** ダイアログを表示させた後、設定アプリへ遷移する関数です
     * @param message String メッセージ
     * @param positiveButtonMessage String ボタン上のテキスト
     * @param context Context コンテキスト
     */

    fun showMessageToSettings (message: String, positiveButtonMessage: String, context: Context) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message)
            .setPositiveButton(positiveButtonMessage, DialogInterface.OnClickListener { dialog, id ->
                // [はい]ボタンを押下した際の処理
                // 設定アプリへ遷移します

                var urlString: String = "package:" + context.packageName
                val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(urlString))
                context.startActivity(intent)

            })

        // ポップアップ画面を表示します
        builder.show()

    }

    /** ダイアログを表示させる関数です
     * @param message String メッセージ
     * @param positiveButtonMessage String ボタン上のテキスト
     * @param context Context コンテキスト
     */

    fun showErrorMessage (message: String, positiveButtonMessage: String, context: Context) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message)
            .setPositiveButton(positiveButtonMessage, DialogInterface.OnClickListener { dialog, id ->
                // [はい]ボタンを押下した際の処理
                // なにもしない
            })

        // ポップアップ画面を表示します
        builder.show()

    }

    /** ダイアログを表示させる関数です
     * @param message String メッセージ
     * @param positiveButtonMessage String ボタン上のテキスト
     * @param context Context コンテキスト
     */

    fun showErrorMessageToPreviousScreen (message: String, positiveButtonMessage: String, context: Context) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message)
            .setPositiveButton(positiveButtonMessage, DialogInterface.OnClickListener { dialog, id ->
                // [はい]ボタンを押下した際の処理
                (context as Activity).finish()
            })

        // ポップアップ画面を表示します
        builder.show()

    }

    /** ダイアログを表示させ、エラーが発生したことを表示する関数です **/
    /*
    fun showErrorMessage (message: String, buttonMessage: String, context: Context, listener: ShowResultForAge) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message).setPositiveButton(buttonMessage, DialogInterface.OnClickListener { dialog, id ->
            //ボタンを押下した際の処理

        })

        //ポップアップ画面を表示します
        builder.show()

    }

     */
}