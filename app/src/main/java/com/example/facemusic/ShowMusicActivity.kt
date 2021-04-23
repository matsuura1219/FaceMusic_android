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
import com.example.facemusic.data.MusicListItem
import kotlinx.android.synthetic.main.activity_show_music.*

/** おすすめの曲を表示するActivityです */

class ShowMusicActivity : Activity(), AdapterView.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_music)

        //戻るボタン
        back.setOnClickListener {
            finish()
        }

        //表示するitemリストを初期化します
        var listItems = ArrayList<MusicListItem>()

        //表示するリストを格納します
        for (item in 1..10) {
            val bitmap = BitmapFactory.decodeResource(this.resources, R.drawable.jacket)
            val item = MusicListItem(this, bitmap, "Official髭男dism", "Pretender")
            listItems.add(item)
        }

        //アダプタを初期化します
        val adapter = MusicListAdapter(this, R.layout.list_item, listItems)
        //listViewにアダプタをセットし、itemを表示します
        listView.setAdapter(adapter)

        //クリックイベントを登録します
        listView.setOnItemClickListener(this)

    }

    /** リストのitemをクリックしたときに呼ばれる関数です */

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        val intent = Intent(this, PlayMusicActivity::class.java)
        startActivity(intent)
    }
}