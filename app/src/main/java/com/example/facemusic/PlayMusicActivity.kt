package com.example.facemusic

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.facemusic.application.MainApplication
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_play_music.*
import kotlinx.android.synthetic.main.activity_play_music.view.*

class PlayMusicActivity : Activity() {

    /** 変数 **/

    private var data: MusicViewModel = MusicViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        //戻るボタン
        back.setOnClickListener {
            finish()
        }

        data = MainApplication.getInstance().getCurrentMusic()

        //ジャケット写真
        jacket.settings.useWideViewPort = true;
        jacket.settings.loadWithOverviewMode = true;
        jacket.loadUrl(data.imageUrl)
        //アーティスト名
        artist.text = data.artist
        //楽曲名
        music.text = data.music

        //音楽をスタートする
        playButton.setOnClickListener {

            SpotifyApiUtil.getInstance().playMusic(data.id)

        }


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

        SpotifyApiUtil.getInstance().stopMusic()

    }
}