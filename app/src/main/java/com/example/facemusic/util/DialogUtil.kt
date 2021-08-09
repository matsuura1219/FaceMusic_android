package com.example.facemusic.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri


/** ポップアップ画面を表示するクラスです */

class DialogUtil {

    //interface
    interface OnClickButton {
        fun onClickPositiveButton()
    }

    //static領域
    companion object {

        private val _instance: DialogUtil = DialogUtil()
        fun getInstance() = _instance

    }

    /** Spotifyアプリがインストールされず、認証が失敗した場合に、ダイアログを表示させた後、Google Playへ遷移させる関数です */
    fun showAlertMessageToGooglePlay (message: String, buttonMessage: String, context: Context, listener: OnClickButton) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message).setPositiveButton(buttonMessage, DialogInterface.OnClickListener { dialog, id ->
            //ボタンを押下した際の処理

            try {

                //Google PlayのSpotify
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.spotify.music&hl=ja&gl=US")))

            } catch (error: android.content.ActivityNotFoundException) {
                // Play Storeアプリを無効にしている場合
                // アプリを終了します
                showCloseMessage("Google Storeアプリが無効になっているため、アプリを終了します", "はい", context, listener)
            }

        })

        //ポップアップ画面を表示します
        builder.show()

    }


    /** ダイアログを表示させた後、アプリケーションを終了する関数です */
    fun showCloseMessage (message: String, buttonMessage: String, context: Context, listener: OnClickButton) {

        val builder = AlertDialog.Builder(context)

        builder.setMessage(message).setPositiveButton(buttonMessage, DialogInterface.OnClickListener { dialog, id ->
            //ボタンを押下した際の処理

        })

        //ポップアップ画面を表示します
        builder.show()

    }
}