package com.example.facemusic

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_play_music.*

class PlayMusicActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)

        //戻るボタン
        back.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
    }
}