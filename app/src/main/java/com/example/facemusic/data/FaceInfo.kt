package com.example.facemusic.data

import android.graphics.Bitmap
import android.hardware.camera2.params.Face

/** 撮影した画像の情報を格納するクラスです **/

class FaceInfo {

    //static領域
    companion object {

        private val instance: FaceInfo = FaceInfo()
        fun getInstance(): FaceInfo {
            return  instance
        }
    }

    /** 変数 **/
    private var photo: Bitmap? = null

}