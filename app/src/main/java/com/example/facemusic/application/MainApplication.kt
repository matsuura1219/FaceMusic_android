package com.example.facemusic.application

import android.app.Application
import android.graphics.Bitmap
import com.example.facemusic.model.MusicViewModel

/** Activity間でのデータの共有を行うためのクラスです **/

class MainApplication: Application() {

    /** 変数 **/
    //DBから取得したviewmodelの配列
    private var musicViewModel: ArrayList<MusicViewModel> = ArrayList<MusicViewModel>()

    //現在選択したviewmodel
    private var currentMusic: MusicViewModel = MusicViewModel()

    //現在再生しているviewmodel
    private var isPlayingMusic: MusicViewModel = MusicViewModel()

    //選択したコンテンツ
    private var selectContent: Int = 0

    //撮影した画像
    private var photo: Bitmap? = null

    //static領域
    companion object {
        //シングルトン
        private val _instance: MainApplication = MainApplication()
        fun getInstance(): MainApplication { return _instance}

    }


    /** 初期化処理 **/
    public fun clearIsPlayingMusic () {

        this.isPlayingMusic = MusicViewModel()

    }

    /** セッター ゲッタ― **/

    public fun setMusicViewModel (model: ArrayList<MusicViewModel>) {
        this.musicViewModel = model
    }

    public fun getMusicViewModel (): ArrayList<MusicViewModel> {
        return musicViewModel
    }

    public fun setCurrentMusic (model: MusicViewModel) {
        this.currentMusic = model
    }

    public fun getCurrentMusic (): MusicViewModel {
        return currentMusic
    }

    public fun setIsPlayingMusic (model: MusicViewModel) {

        this.isPlayingMusic = model
    }

    public fun getIsPlayingMusic (): MusicViewModel {
        return isPlayingMusic
    }

    public fun setSelectContent (content: Int) {
        this.selectContent = content

    }

    public fun getSelectContent (): Int {
        return this.selectContent
    }

    public fun setPhoto (photo: Bitmap) {
        this.photo = photo
    }

    public fun getPhoto (): Bitmap? {
        return this.photo
    }

}