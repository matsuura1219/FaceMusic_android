package com.example.facemusic

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.amazonaws.services.securitytoken.model.PackedPolicyTooLargeException
import com.example.facemusic.`interface`.SpotifyIsPlayingListener
import com.example.facemusic.adapter.MusicListAdapter
import com.example.facemusic.application.MainApplication
import com.example.facemusic.data.MusicListItem
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_show_music.*
import kotlinx.android.synthetic.main.activity_show_music.back
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** おすすめの曲を表示するActivityです */

class ShowMusicActivity : Activity(), AdapterView.OnItemClickListener, SpotifyIsPlayingListener {

    /** 変数 **/
    private var listItems: ArrayList<MusicViewModel> = ArrayList<MusicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_music)


        //戻るボタン
        back.setOnClickListener {
            finish()
        }

        //共通領域から楽曲リストを取得します
        val myApp: MainApplication = MainApplication.getInstance()
        listItems = myApp.getMusicViewModel()

        //アダプタを初期化します
        val adapter = MusicListAdapter(this, R.layout.list_item, listItems)
        //listViewにアダプタをセットし、itemを表示します
        listView.adapter = adapter

        //クリックイベントを登録します
        listView.onItemClickListener = this

    }

    override fun onResume() {
        super.onResume()

        SpotifyApiUtil.getInstance().isPlaying(this)


    }

    /** リストのitemをクリックしたときに呼ばれる関数です */

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        //共通領域に選択した楽曲データをセットします
        MainApplication.getInstance().setCurrentMusic(listItems.get(p2))

        val intent = Intent(this, PlayMusicActivity::class.java)
        startActivity(intent)
    }


    /** 曲が再生されているかどうかを判定するコールバック関数です **/
    override fun onIsPlayingResponse(isPlaying: Boolean) {

        //MainスレッドでUIを更新します
        val cor = CoroutineScope(Dispatchers.Main)

        cor.launch {

            if (isPlaying) {
                //曲が再生されている場合

                musicBox.visibility = View.VISIBLE
                artist_playing.text = MainApplication.getInstance().getCurrentMusic().artist
                music_playing.text = MainApplication.getInstance().getCurrentMusic().music
                //ジャケット写真
                photo_playing.settings.useWideViewPort = true;
                photo_playing.settings.loadWithOverviewMode = true;
                photo_playing.loadUrl(MainApplication.getInstance().getCurrentMusic().imageUrl)

            } else {

                musicBox.visibility = View.INVISIBLE

            }

        }

    }

}