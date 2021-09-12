package com.example.facemusic.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.example.facemusic.R
import com.example.facemusic.`interface`.SpotifyGetCurrentMusicPosition
import com.example.facemusic.`interface`.SpotifyIsPlayingListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Constants
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.service.SpotifyApiClient
import kotlinx.android.synthetic.main.activity_play_music.*
import kotlinx.android.synthetic.main.activity_play_music.playButton
import kotlinx.android.synthetic.main.activity_play_music.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import javax.net.ssl.HandshakeCompletedListener

/** 楽曲を再生するActivityです */



class PlayMusicActivity : Activity(), SeekBar.OnSeekBarChangeListener,
    SpotifyGetCurrentMusicPosition, View.OnClickListener {

    /** 定数 **/

    private val TEN_SECOND: Long = 10000

    /** 変数 **/

    // 表示しているmodel
    private var data: MusicViewModel = MusicViewModel()
    // 現在、音楽を再生中かを判定するフラグ
    private var isPlaying: Boolean = false
    // 楽曲を再生したことがあるかを判定するフラグ
    private var havePlayed: Boolean = false
    // ハンドラ
    private var handler: Handler = Handler(Looper.getMainLooper())
    // タイマー
    private var timer: Timer? = null
    // ドラッグ前のseekBarの位置
    private var positionBeforeDrag: Int = 0
    // 曲の再生時間
    private var duration: Long = 0L
    // 画面遷移時に受け取ったデータ
    private var receiveDataFromScreen: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        // 初期化処理を行います
        init()

        // 再生時間を設定します
        duration = data.durationTime.toLong()

        // Viewの初期化処理を行います
        initComponent()

        // 画面遷移時に渡した値を受け取り、設定します
        receiveDataFromScreen = intent.getStringExtra(Constants.KEY)!!

        if (!MainApplication.getInstance().getUserInfo().getIsPlaying()) {
            // アプリ内で曲が再生されていない場合
            // 曲を再生します
            playMusic()

        } else {

            if (receiveDataFromScreen.equals(Constants.LISTVIEW_CLICK)) {
                // ListViewから画面遷移を行った場合
                // 曲を再生します
                playMusic()

            } else {
                // スナックバーから画面遷移を行った場合
                // 曲の再生位置を取得します
                SpotifyApiClient.getInstance().getCurrentMusicPosition(this)
                // フラグの設定をします
                isPlaying = true
                havePlayed = true

                // 「再生」ボタンを「停止」ボタンに変更します
                playButton.setImageResource(R.drawable.stop)

            }
        }

    }


    /** 初期化を行う関数です **/
    private fun init () {

        // イベント設定
        back.setOnClickListener(this)
        playButton.setOnClickListener(this)
        skipButton.setOnClickListener(this)
        backButton.setOnClickListener(this)
        rewindButton.setOnClickListener(this)
        forwardButton.setOnClickListener(this)
        seekBar.setOnSeekBarChangeListener(this)

        // 選択したMusicViewModelを取得します
        data = MainApplication.getInstance().getCurrentMusic()

    }


    /** Viewの初期化処理 **/
    private fun initComponent () {

        // ジャケット写真を表示させます
        jacket.settings.useWideViewPort = true;
        jacket.settings.loadWithOverviewMode = true;
        jacket.loadUrl(data.imageUrl)

        // アーティスト名を表示させます
        artist.text = data.artist

        // 楽曲名を表示させます
        music.text = data.music

    }


    /** ボタン押下時に実行されるコールバック関数です **/
    override fun onClick(p0: View) {

        if (p0.id == R.id.back) {
            // backボタン押下時
            finish()

        } else if (p0.id == R.id.playButton) {
            // 再生ボタン押下時

            if (isPlaying) {
                // 音楽を再生中の場合
                // 音楽を停止します
                stopMusic()

            } else {
                // 音楽を再生していない場合

                if (havePlayed) {
                    // 1度音楽を再生した場合
                    // 曲を途中から再生します
                    resumeMusic()

                } else {
                    // 始めて音楽を再生する場合
                    // 最初から音楽を再生します
                    playMusic()

                }

            }
        } else if (p0.id == R.id.skipButton) {
            // 次へボタンを押下した場合
            // 次の楽曲を再生します
            changeToNextMusic(true)
        } else if (p0.id == R.id.backButton) {
            // 戻るボタンを押下した場合
            // 前の楽曲を再生します
            changeToNextMusic(false)
        } else if (p0.id == R.id.rewindButton) {
            // 巻き戻しボタンを押下した場合
            SpotifyApiClient.getInstance().changeMusicPosition(TEN_SECOND * (-1))
            // タイマーを止めます
            positionBeforeDrag = seekBar.progress
            stopSeekBarPosition()
            // 曲の再生位置を取得します
            SpotifyApiClient.getInstance().getCurrentMusicPosition(this)

        } else if (p0.id == R.id.forwardButton) {
            // 早送りボタンを押下した場合
            SpotifyApiClient.getInstance().changeMusicPosition(TEN_SECOND)
            // タイマーを止めます
            positionBeforeDrag = seekBar.progress
            stopSeekBarPosition()
            // 曲の再生位置を取得します
            SpotifyApiClient.getInstance().getCurrentMusicPosition(this)
        }

    }

    /** 次の楽曲を再生する関数です **/
    private fun changeToNextMusic (doSkip: Boolean) {

        val model: MusicViewModel

        // 次に再生する楽曲Model
        if (doSkip) {
            // スキップボタンを押下した場合
            val listPosition = MainApplication.getInstance().getUserInfo().getSelectedPosition()
            model = MainApplication.getInstance().getMusicViewModel()[listPosition + 1]
            MainApplication.getInstance().getUserInfo().setSelectedPosition(listPosition + 1)
        } else {
            // 戻るボタンを押下した場合
            val listPosition = MainApplication.getInstance().getUserInfo().getSelectedPosition()
            model = MainApplication.getInstance().getMusicViewModel()[listPosition - 1]
            MainApplication.getInstance().getUserInfo().setSelectedPosition(listPosition - 1)
        }

        // 共通領域を更新します
        MainApplication.getInstance().setCurrentMusic(model)

        // シークバーの位置を初期値に戻します
        positionBeforeDrag = 0

        data = MainApplication.getInstance().getCurrentMusic()

        // ジャケット写真を表示させます
        jacket.loadUrl(data.imageUrl)

        // アーティスト名を表示させます
        artist.text = data.artist

        // 楽曲名を表示させます
        music.text = data.music

        // 曲を再生します
        SpotifyApiClient.getInstance().playMusic(data.id, this)

        // シークバーアニメーションを停止させます
        stopSeekBarPosition()

        // シークバーを初期位置に設定し、アニメーションを開始します
        changeSeekBarPosition(0)

    }


    /** 曲を再生する関数です **/
    private fun playMusic () {

        // 「再生」ボタンを「停止」ボタンに変更します
        playButton.setImageResource(R.drawable.stop)
        // 指定の曲を再生します
        SpotifyApiClient.getInstance().playMusic(data.id, this)
        // seekBarの位置を自動で変更します
        changeSeekBarPosition(0)

        // フラグを更新します
        isPlaying = true
        havePlayed = true

        // 共通領域に設定します
        MainApplication.getInstance().getUserInfo().setIsPlaying(true)
        MainApplication.getInstance().setCurrentMusic(data)

    }


    /** 曲を停止する関数です **/
    private fun stopMusic () {

        // 「停止」ボタンを「再生」ボタンに変更します
        playButton.setImageResource(R.drawable.play)
        // 曲を停止させます
        SpotifyApiClient.getInstance().stopMusic()
        // seekBarの自動アニメーションを停止させます
        stopSeekBarPosition()

        // 共通領域に設定します
        MainApplication.getInstance().getUserInfo().setIsPlaying(false)
        MainApplication.getInstance().setCurrentMusic(MusicViewModel())

        // フラグを更新します
        isPlaying = false

    }

    /** 曲を途中から再生する関数です **/
    private fun resumeMusic () {

        // 「再生」ボタンを「停止」ボタンに変更します
        playButton.setImageResource(R.drawable.stop)
        // 曲を途中から再生します
        SpotifyApiClient.getInstance().resumePlayMusic()
        // seekBarの位置を自動で変更します
        changeSeekBarPosition(seekBar.progress)

        // 共通領域に設定します
        MainApplication.getInstance().getUserInfo().setIsPlaying(true)

        // フラグを更新します
        isPlaying = true

    }


    /** シークバーを自動で移動させる関数です **/
    private fun changeSeekBarPosition(position: Int) {

        var cPosition = position
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    cPosition += 1
                    seekBar.progress = cPosition
                }
            }
        }, 0, duration / 100)
    }

    /** シークバーを停止させる関数です **/
    private fun stopSeekBarPosition () {
        timer?.cancel()
        timer = null
    }



    /** つまみがドラックされると呼ばれるコールバック関数です */
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
    }



    /** つまみがタッチされたときに呼ばれるコールバック関数です */
    override fun onStartTrackingTouch(p0: SeekBar?) {

        positionBeforeDrag = p0!!.progress
        //seekBarの自動アニメーションを停止させます
        stopSeekBarPosition()

    }


    /** つまみがリリースされた時に呼ばれるコールバック関数です */
    override fun onStopTrackingTouch(p0: SeekBar?) {

        // ドラッグ前後のseekBarの位置の差
        var changeMusicPosition: Int = p0!!.progress - positionBeforeDrag
        // 曲の位置を変更します
        SpotifyApiClient.getInstance()
            .changeMusicPosition(changeMusicPosition.toLong() * data.durationTime / 100)
        // seekBarの位置を自動で変更します
        changeSeekBarPosition(seekBar.progress)

    }

    /** 曲の再生位置を取得したのちに実行されるコールバック関数です **/
    override fun getCurrentMusicPosition(position: Long) {

        changeSeekBarPosition((position * 100 / data.durationTime).toInt())

    }

}