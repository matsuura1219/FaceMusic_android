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
import com.example.facemusic.adapter.MusicListAdapter
import com.example.facemusic.application.MainApplication
import com.example.facemusic.data.MusicListItem
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_show_music.*
import kotlinx.android.synthetic.main.activity_show_music.back
import kotlinx.android.synthetic.main.activity_show_music.music

/** おすすめの曲を表示するActivityです */

class ShowMusicActivity : Activity(), AdapterView.OnItemClickListener {

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

        if (!MainApplication.getInstance().getCurrentMusic().artist.equals("")) {
            musicBox.visibility = View.VISIBLE
            artist.text = MainApplication.getInstance().getCurrentMusic().artist
            music.text = MainApplication.getInstance().getCurrentMusic().music
            //ジャケット写真
            photo.settings.useWideViewPort = true;
            photo.settings.loadWithOverviewMode = true;
            photo.loadUrl(MainApplication.getInstance().getCurrentMusic().imageUrl)

        }
    }

    /** リストのitemをクリックしたときに呼ばれる関数です */

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        //共通領域に選択した楽曲データをセットします
        MainApplication.getInstance().setCurrentMusic(listItems.get(p2))

        val intent = Intent(this, PlayMusicActivity::class.java)
        startActivity(intent)
    }

}