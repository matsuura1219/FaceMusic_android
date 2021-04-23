package com.example.facemusic.util

/** Firebaseの操作を行うためのクラスです */

class FirebaseUtil {

    //シングルトン
    companion object {

        private var  _instance: FirebaseUtil = FirebaseUtil()
        fun getInstance(): FirebaseUtil = _instance
    }


}