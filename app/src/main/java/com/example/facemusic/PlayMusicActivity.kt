package com.example.facemusic

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.example.facemusic.application.MainApplication
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_play_music.*
import kotlinx.android.synthetic.main.activity_play_music.view.*
import java.sql.Time
import java.util.*
import javax.net.ssl.HandshakeCompletedListener

/** 楽曲を再生するActivityです */

class PlayMusicActivity : Activity(), SeekBar.OnSeekBarChangeListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        //戻るボタン
        back.setOnClickListener {
            finish()
        }

        //インターフェースを実装する
        seekBar.setOnSeekBarChangeListener(this)

        //楽曲情報
        data = MainApplication.getInstance().getCurrentMusic()

        //ジャケット写真
        jacket.settings.useWideViewPort = true;
        jacket.settings.loadWithOverviewMode = true;
        jacket.loadUrl(data.imageUrl)

        //アーティスト名
        artist.text = data.artist
        //楽曲名
        music.text = data.music

        //「再生」もしくは「停止」ボタンをクリックする
        playButton.setOnClickListener {

            if (!isPlay) {
                //まだ曲が再生されていない場合
                isPlay = true

                if (!havePlayed) {
                    //その曲を1度も再生していない場合
                    //曲を初めから再生します
                    playMusic()
                    havePlayed = true

                } else {
                    //曲を停止するなどして、1度曲を再生した場合
                    //曲を途中から再生する
                    resumeMusic()

                }

            } else {
                //曲が再生されている場合
                isPlay = false
                //曲を停止します
                stopMusic()

            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }


    /** seekBarを曲に応じて、変更させる関数です
     * @param position 現在のseekBarの位置
     * */

    private fun changeSeekBarPosition (position: Int) {

        var cPosition = position

        timer = Timer()
        timer!!.schedule(object: TimerTask() {

            override fun run() {

                handler.post {

                    cPosition += 1
                    seekBar.progress = cPosition

                }

            }
        }, 0, 280000 / 100)

    }
    
    /** seekBarの自動アニメーションを停止させる関数です */
    private fun stopSeekBarPosition () {

        timer?.cancel()
        timer = null
    }



    /** 曲を再生する関数です */
    private fun playMusic () {

        //「再生」ボタンを「停止」ボタンに変更します
        playButton.setImageResource(R.drawable.stop)
        //指定の曲を再生します
        SpotifyApiUtil.getInstance().playMusic(data.id)
        //seekBarの位置を自動で変更します
        changeSeekBarPosition(0)


    }

    /** 曲を再度再生する関数です */
    private fun resumeMusic () {

        //「再生」ボタンを「停止」ボタンに変更します
        playButton.setImageResource(R.drawable.stop)
        //曲を途中から再生します
        SpotifyApiUtil.getInstance().resumePlayMusic()
        //seekBarの位置を自動で変更します
        changeSeekBarPosition(seekBar.progress)

    }

    /** 曲を停止する関数です */
    private fun stopMusic () {
        //「停止」ボタンを「再生」ボタンに変更します
        playButton.setImageResource(R.drawable.play)
        //曲を停止させます
        SpotifyApiUtil.getInstance().stopMusic()
        //seekBarの自動アニメーションを停止させます
        stopSeekBarPosition()

    }


    /** 次の曲を再生する際の関数です */
    private fun playNextButton () {

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
        SpotifyApiUtil.getInstance().changeMusicPosition(changeMusicPosition.toLong() * 280000 / 100)
        //seekBarの位置を自動で変更します
        changeSeekBarPosition(seekBar.progress)

    }




}