package com.example.facemusic.application

import android.app.Application
import com.example.facemusic.data.UserInfo
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.service.SpotifyApiClient

/** Activity間でのデータの共有を行うためのクラスです **/

class MainApplication: Application() {

    /** 変数 **/

    // ユーザの情報
    private var userInfo: UserInfo = UserInfo()

    // DBから取得したviewmodelの配列
    private var musicViewModel: ArrayList<MusicViewModel> = ArrayList<MusicViewModel>()

    //　現在選択したviewmodel
    private var currentMusic: MusicViewModel = MusicViewModel()

    //static領域
    companion object {
        //シングルトン
        private val instance: MainApplication = MainApplication()
        fun getInstance(): MainApplication { return instance}

    }

    /** 起動時に実行される関数です **/
    override fun onCreate() {
        super.onCreate()
    }


    /** セッター ゲッタ― **/

    public fun setUserInfo (info: UserInfo) {
        this.userInfo = info
    }

    public fun getUserInfo (): UserInfo {
        return userInfo
    }

    public fun setMusicViewModel (model: ArrayList<MusicViewModel>) {
        this.musicViewModel = model
    }

    public fun getMusicViewModel (): ArrayList<MusicViewModel> {
        return musicViewModel
    }

    /** MusicViewModelのリストを追加する関数です **/
    public fun addMusicViewModel (lists: ArrayList<MusicViewModel>) {
        this.musicViewModel.addAll(lists)
    }

    public fun setCurrentMusic (model: MusicViewModel) {
        this.currentMusic = model
    }

    public fun getCurrentMusic (): MusicViewModel {
        return currentMusic
    }

}