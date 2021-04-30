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
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.SpotifyApiUtil
import kotlinx.android.synthetic.main.activity_show_music.*

/** おすすめの曲を表示するActivityです */

class ShowMusicActivity : Activity(), AdapterView.OnItemClickListener {

    //viewmodel
    private var models: Array<MusicViewModel?> = arrayOfNulls<MusicViewModel?>(10)

    //楽曲id
    private var ids: Array<String?> = arrayOfNulls<String?>(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_music)

        //楽曲idをセットします
        setIds()
        //modelに値をセットします
        setModels()

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

    /** α版用関数(Modelに値をセットします) **/
    private fun setModels () {

        var counter: Int = 0

        for (model in models) {


        }


    }

    /** α版用関数(idsに値をセットします) **/
    private fun setIds () {

        ids[0] = "7dH0dpi751EoguDDg3xx6J"
        ids[1] = "4saklk6nie3yiGePpBwUoc"
        ids[2] = "6MCjmGYlw6mQVWRFVgBRvB"
        ids[3] = "0XZyF9lv6diMt4bxThOL0h"
        ids[4] = "1zd35Y44Blc1CwwVbW3Qnk"
        ids[5] = "06XQvnJb53SUYmlWIhUXUi"
        ids[6] = "0iEzzJ3zmNQqATy2MilumS"
        ids[7] = "6EzZn96uOc9JsVGNRpx06n"
        ids[8] = "29VVYrV5TVpGu0IfoTXlcw"
        ids[9] = "3UHPGOkUcE4hE7sqBF4Snt"

    }
}