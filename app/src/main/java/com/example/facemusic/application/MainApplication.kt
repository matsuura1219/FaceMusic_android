package com.example.facemusic.application

import android.app.Application
import com.example.facemusic.model.MusicViewModel

/** Activity間でのデータの共有を行うためのクラスです **/

class MainApplication: Application() {

    /** 変数 **/
    //DBから取得したviewmodelの配列
    private var musicViewModel: ArrayList<MusicViewModel> = ArrayList<MusicViewModel>()
    //現在選択したviewmodel
    private var currentMusic: MusicViewModel = MusicViewModel()

    //static領域
    companion object {
        //シングルトン
        private val _instance: MainApplication = MainApplication()
        fun getInstance(): MainApplication { return _instance}

    }

    /** セッター・ゲッタ― **/

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
}