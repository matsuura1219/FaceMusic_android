package com.example.facemusic.data

import android.graphics.Bitmap
import com.example.facemusic.json.faceapi.FaceApiData

/** ユーザの情報を格納するするクラスです **/

class UserInfo {

    // ユーザが選択したコンテンツ
    private var selectContent: String = ""
    // 撮影画像
    private var photo: Bitmap? = null
    // FaceAPIの返却データ
    private var faceApiData: FaceApiData? = null
    // 曲が再生中かどうかを判定するフラグ
    private var isPlaying: Boolean = false
    // 選択したListViewの位置
    private var selectedPosition: Int = 0

    public fun setSelectContent (content: String) {
        this.selectContent = content
    }

    public fun getSelectContent (): String {
        return selectContent
    }

    public fun setPhoto (photo: Bitmap) {
        this.photo = photo
    }

    public fun getPhoto (): Bitmap? {
        return  photo
    }

    public fun setFaceApiData (data: FaceApiData) {
        this.faceApiData = data
    }

    public fun getFaceApiData (): FaceApiData? {
        return faceApiData
    }

    public fun getIsPlaying (): Boolean {
        return  this.isPlaying
    }

    public fun setIsPlaying (isPlaying: Boolean) {
        this.isPlaying = isPlaying
    }

    public fun getSelectedPosition (): Int {
        return this.selectedPosition
    }

    public fun setSelectedPosition (position: Int) {
        this.selectedPosition = position
    }



}