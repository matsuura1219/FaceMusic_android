package com.example.facemusic

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
import com.example.facemusic.`interface`.SpotifyGetCurrentMusicPosition
import com.example.facemusic.`interface`.SpotifyIsPlayingListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.SpotifyApiUtil
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

class PlayMusicActivity : Activity(), SeekBar.OnSeekBarChangeListener, SpotifyIsPlayingListener,
    SpotifyGetCurrentMusicPosition, View.OnClickListener {

    /** 変数 **/

    private var data: MusicViewModel = MusicViewModel()

    //ドラッグ前のseekBarの位置
    private var positionBeforeDrag: Int = 0

    //曲が再生されているかどうか
    private var isPlay: Boolean = false

    //曲を再生したことがあるかどうか
    private var havePlayed: Boolean = false

    //ハンドラ
    private var handler: Handler = Handler(Looper.getMainLooper())

    //タイマー
    private var timer: Timer? = null

    //仮変数
    private val MUSIC_TIME: Long = 280000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        //初期化処理を行います
        init()

        //Viewの初期化処理を行います
        initComponent()

        //現在の曲の再生状況を確認します
        getCurrentSituation()


    }


    /** 初期化を行う関数です **/
    private fun init () {

        //イベント設定
        back.setOnClickListener(this)
        playButton.setOnClickListener(this)
        seekBar.setOnSeekBarChangeListener(this)

        //選択したMusicViewModelを取得します
        data = MainApplication.getInstance().getCurrentMusic()

    }


    /** Viewの初期化処理 **/
    private fun initComponent () {

        var receiveData: String? = intent.getStringExtra("data")

        if (receiveData != null) {

            if (receiveData.equals("onItemClick")) {

                data = MainApplication.getInstance().getCurrentMusic()

            } else if (receiveData.equals("moveToPlayActivity")) {

                data = MainApplication.getInstance().getIsPlayingMusic()

            }

        }

        //ジャケット写真
        jacket.settings.useWideViewPort = true;
        jacket.settings.loadWithOverviewMode = true;
        jacket.loadUrl(data.imageUrl)

        //アーティスト名
        artist.text = data.artist
        //楽曲名
        music.text = data.music

    }

    /** 現在の再生状況の確認を行う関数です **/
    private fun getCurrentSituation () {

        //現在曲が再生中かを確認します
        SpotifyApiUtil.getInstance().isPlaying(this)

    }


    /** 曲を再生する関数です **/
    private fun playMusic () {

        //「再生」ボタンを「停止」ボタンに変更します
        playButton.setImageResource(R.drawable.stop)
        //指定の曲を再生します
        SpotifyApiUtil.getInstance().playMusic(data.id)
        //seekBarの位置を自動で変更します
        changeSeekBarPosition(0)

        isPlay = true
        havePlayed = true

        //共通領域に設定します
        MainApplication.getInstance().setIsPlayingMusic(data)

    }


    /** 曲を停止する関数です **/
    private fun stopMusic () {

        //「停止」ボタンを「再生」ボタンに変更します
        playButton.setImageResource(R.drawable.play)
        //曲を停止させます
        SpotifyApiUtil.getInstance().stopMusic()
        //seekBarの自動アニメーションを停止させます
        stopSeekBarPosition()

        //共通領域に設定します
        MainApplication.getInstance().clearIsPlayingMusic()

        isPlay = false

    }

    /** 曲を途中から再生する関数です **/
    private fun resumeMusic () {

        //「再生」ボタンを「停止」ボタンに変更します
        playButton.setImageResource(R.drawable.stop)
        //曲を途中から再生します
        SpotifyApiUtil.getInstance().resumePlayMusic()
        //seekBarの位置を自動で変更します
        changeSeekBarPosition(seekBar.progress)

        //共通領域に設定します
        MainApplication.getInstance().setIsPlayingMusic(data)

        isPlay = true


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
        }, 0, MUSIC_TIME / 100)


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

        //ドラッグ前後のseekBarの位置の差
        var changeMusicPosition: Int = p0!!.progress - positionBeforeDrag
        //曲の位置を変更します
        SpotifyApiUtil.getInstance()
            .changeMusicPosition(changeMusicPosition.toLong() * MUSIC_TIME / 100)
        //seekBarの位置を自動で変更します
        changeSeekBarPosition(seekBar.progress)

    }


    /** 曲が再生されているか判定したのちに実行されるコールバック関数です **/
    override fun onIsPlayingResponse(isPlaying: Boolean) {


        if (isPlaying) {
            //曲が再生されている場合

            if (!MainApplication.getInstance().getIsPlayingMusic().artist.equals("")) {
                //本アプリ内で現在曲が再生されている場合 （Spotifyアプリは除く）
                //途中から曲を再生します

                isPlay = true
                havePlayed = true

                if (MainApplication.getInstance().getCurrentMusic().artist.equals(MainApplication.getInstance().getIsPlayingMusic().artist)
                    && MainApplication.getInstance().getCurrentMusic().music.equals(MainApplication.getInstance().getIsPlayingMusic().music)) {

                    val cor = CoroutineScope(Dispatchers.Main)

                    cor.launch {

                        //再生中の曲と表示する画面が同一の場合
                        playButton.setImageResource(R.drawable.stop)

                    }


                    //曲の再生位置を取得します
                    SpotifyApiUtil.getInstance().getCurrentMusicPosition(this)


                } else {

                    val cor = CoroutineScope(Dispatchers.Main)

                    cor.launch {

                        stopMusic()
                        playMusic()

                    }


                }


            } else {

                val cor = CoroutineScope(Dispatchers.Main)

                cor.launch {

                    //楽曲を再生します
                    playMusic()

                }

            }

        } else {

            val cor = CoroutineScope(Dispatchers.Main)

            cor.launch {

                //楽曲を再生します
                playMusic()

            }

        }


        /*
        //MainスレッドでUIを更新します
        val cor = CoroutineScope(Dispatchers.Main)

        cor.launch {

            if (isPlaying) {
                //曲が再生されている場合

                if (!MainApplication.getInstance().getIsPlayingMusic().artist.equals("")) {
                    //本アプリ内で現在曲が再生されている場合 （Spotifyアプリは除く）
                    //途中から曲を再生します
                    //resumeMusic()

                    isPlay = true
                    havePlayed = true

                    if (MainApplication.getInstance().getCurrentMusic().artist.equals(MainApplication.getInstance().getIsPlayingMusic().artist)
                        && MainApplication.getInstance().getCurrentMusic().music.equals(MainApplication.getInstance().getIsPlayingMusic().music)) {

                        //再生中の曲と表示する画面が同一の場合
                        playButton.setImageResource(R.drawable.stop)

                        //曲の再生位置を取得します
                        SpotifyApiUtil.getInstance().getCurrentMusicPosition(this)


                    } else {

                        stopMusic()
                        playMusic()
                    }


                } else {

                    //楽曲を再生します
                    playMusic()

                }

            } else {

                //楽曲を再生します
                playMusic()

            }

        }

         */


    }


    /** 曲の再生位置を取得したのちに実行されるコールバック関数です **/
    override fun getCurrentMusicPosition(position: Long) {

        changeSeekBarPosition((position * 100 / MUSIC_TIME).toInt())

    }


    /** ボタン押下時に実行されるコールバック関数です **/
    override fun onClick(p0: View) {


        if (p0.id == R.id.back) {
          //backボタン押下時
            finish()

        } else if (p0.id == R.id.playButton) {
            //再生ボタン押下時

            if (isPlay) {
                stopMusic()

            } else {

                if (havePlayed) {

                    resumeMusic()

                } else {

                    playMusic()

                }


            }
        }

    }
}