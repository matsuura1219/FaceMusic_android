package com.example.facemusic.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat.getSystemService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


/** 共通関数をまとめたクラスです **/

class CommonUtil {

    //static領域
    companion object {


        /** アプリを強制終了する関数です **/

        fun finishApp (activity: Activity) {

            activity.finishAndRemoveTask()

        }

        /** Bitmap型をFile型に変更する関数です **/

        fun createUploadFile(context: Context, bitmap: Bitmap, fileName: String): File? {

            val file = File(File(context.externalCacheDir.toString()), fileName)

            var fos: FileOutputStream? = null

            try {

                file.createNewFile()
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

            } catch (e: IOException) {
                e.printStackTrace()
                // TODO handle error
            } finally {
                try {
                    if (fos != null) {
                        fos.flush()
                        fos.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    // TODO handle error
                }
            }
            return file
        }

        /** 時刻を文字列で返す関数です **/

        fun getRandomNumber (): String {

            //戻り値として返すフォーマットを作成します
            val df = SimpleDateFormat("yyyyMMddHHmmss")
            //現在の時刻から指定のフォーマットに変換します
            val date = Date(System.currentTimeMillis())

            return df.format(date) + ".jpeg"

        }

        /** 画像URLをBitmapに変換する関数です **/

        fun getBitmapFromUrl (path: String): Bitmap? {

            try {

                val url: URL = URL(path)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connect()
                val input = connection.inputStream
                val bitmap: Bitmap = BitmapFactory.decodeStream(input)
                return bitmap

            } catch (ie: IOException) {

                ie.printStackTrace()
                return null
            }
        }


        /** トリミングを行う関数です */

        fun trimingPhoto (photo: Bitmap, top: Int, left: Int, width: Int, height: Int): Bitmap {

            val croppedPhoto: Bitmap = Bitmap.createBitmap(photo, left, top, width, height)

            return croppedPhoto

        }
    }

}