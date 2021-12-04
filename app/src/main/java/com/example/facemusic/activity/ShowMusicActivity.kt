package com.example.facemusic.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import com.amazonaws.services.securitytoken.model.PackedPolicyTooLargeException
import com.example.facemusic.R
import com.example.facemusic.`interface`.EC2ServerListener
import com.example.facemusic.`interface`.SpotifyIsPlayingListener
import com.example.facemusic.adapter.MusicListAdapter
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Constants
import com.example.facemusic.data.MusicListItem
import com.example.facemusic.data.UserInfo
import com.example.facemusic.json.FaceMusicApiData
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.service.EC2Client
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_show_music.*
import kotlinx.android.synthetic.main.activity_show_music.back
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.activity_play_music.*


/** おすすめの曲を表示するActivityです */

class ShowMusicActivity : Activity(), AdapterView.OnItemClickListener,
    AbsListView.OnScrollListener, EC2ServerListener, View.OnClickListener {

    /** 定数 **/

    // ListViewに表示させる最大のカラム数
    private val MAX_LIST_COUNT: Int = 100

    /** 変数 **/

    // ListViewを管理するadapter
    private var adapter: MusicListAdapter? = null
    // ListViewに表示させているModel
    private var listItems: ArrayList<MusicViewModel> = ArrayList<MusicViewModel>()
    // APIをコール中かを判定するフラグ
    private var isCallingAPi: Boolean = false
    // WebApiコール時のDB検索位置
    private var dbSearchPosition: Int = Constants.DB_SEARCH_COUNT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_music)

        // 共通領域から楽曲リストを取得します
        val myApp: MainApplication = MainApplication.getInstance()
        listItems = myApp.getMusicViewModel()

        // アダプタを初期化します
        adapter = MusicListAdapter(this, R.layout.list_item, listItems)
        // listViewにアダプタをセットし、itemを表示します
        listView.adapter = adapter

        // リスナーを設定します
        listView.setOnScrollListener(this)
        listView.onItemClickListener = this
        music_snackbar.setOnClickListener(this)
        back.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()

        if (MainApplication.getInstance().getUserInfo().getIsPlaying()) {
            // アプリ内で曲が再生中の場合
            // スナックバーを表示させます
            music_snackbar.visibility = View.VISIBLE
            photo_playing.settings.useWideViewPort = true;
            photo_playing.settings.loadWithOverviewMode = true;
            photo_playing.loadUrl(MainApplication.getInstance().getCurrentMusic().imageUrl)
            artist_playing.text = MainApplication.getInstance().getCurrentMusic().artist
            music_playing.text = MainApplication.getInstance().getCurrentMusic().music

        } else {
            music_snackbar.visibility = View.INVISIBLE
        }

    }

    /** ListViewのアイテムをクリックした場合に実行されるコールバック関数です
     * @param p0 AdapterView<*>? adapter
     * @param p1 View? 選択されたView
     * @param p2 Int 選択されたViewの位置
     * @param p3 Long 選択されたViewのID
     */
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        // 選択したModel
        val model: MusicViewModel = listItems[p2]

        // 共通領域を更新します
        MainApplication.getInstance().setCurrentMusic(model)

        // 画面遷移を行います
        val intent = Intent(this, PlayMusicActivity::class.java)
        // 値を渡します
        intent.putExtra(Constants.KEY, Constants.LISTVIEW_CLICK)
        // 共通領域を更新します
        MainApplication.getInstance().getUserInfo().setSelectedPosition(p2)
        startActivity(intent)


    }

    /** スクロールした際に実行されるコールバック関数です
     * @param p0 AbsListView? ListView
     * @param p1 Int 表示中の先頭のカラム数
     * @param p2 Int 画面に表示されているカラム数
     * @param p3 Int ListViewに表示させている合計のカラム数
     */
    override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

        if ((p3 - p2) == p1 && !isCallingAPi) {
            // スクロールが完了した場合

            if (p3 < MAX_LIST_COUNT) {
                // 合計のカラム数が MAX_LIST_COUNT を超えない場合

                // ダイアログを表示させます
                dialog.visibility = View.VISIBLE

                if (MainApplication.getInstance().getUserInfo().getSelectContent().equals(Constants.EMOTION_DETECTION)) {

                    var emotion = MainApplication.getInstance().getUserInfo().getFaceApiData()!!.faceAttributes.emotion
                    // APIをコールします
                    EC2Client.getInstance().getMusicForEmtion(
                        emotion.anger.toFloat() * 100,
                        emotion.contempt.toFloat() * 100,
                        emotion.disgust.toFloat() * 100,
                        emotion.fear.toFloat() * 100,
                        emotion.happiness.toFloat() * 100,
                        emotion.neutral.toFloat() * 100,
                        emotion.sadness.toFloat() * 100,
                        emotion.surprise.toFloat() * 100,
                        dbSearchPosition,
                        dbSearchPosition + Constants.DB_SEARCH_COUNT,this)

                } else {

                    var age = MainApplication.getInstance().getUserInfo().getFaceApiData()!!.faceAttributes.age.toInt().toString()
                    EC2Client.getInstance().getMusicForAge(age, dbSearchPosition, dbSearchPosition + Constants.DB_SEARCH_COUNT,this)

                }


                dbSearchPosition += Constants.DB_SEARCH_COUNT

                // フラグを設定します
                isCallingAPi = true

            }
        }

    }

    /** ListViewがスクロール注かどうかを判定するコールバック関数です **/
    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {

    }

    /** EC2インスタンスとの通信に成功し、jsonデータを正常に取得できた場合に実行されるコールバック関数です
     * @param data String 減却されたjsonデータ
     */
    override fun onSuccess(data: String?) {

        // jsonデータをパースします
        val mapper = jacksonObjectMapper()
        val jsonData = mapper.readValue<ArrayList<FaceMusicApiData>>(data!!)

        // 共通領域に値を設定します
        val model = listToViewModel(jsonData)

        //メインスレッドで描画処理を行います
        val coroutine = CoroutineScope(Dispatchers.Main)

        coroutine.launch {

            for (i in 0 until model.size) {

                // ListViewのカラムを追加します
                adapter!!.add(model[i])

            }

            // 共通領域を更新します
            MainApplication.getInstance().addMusicViewModel(model)

            // listViewを更新します
            adapter!!.notifyDataSetChanged()

            // ダイアログを非表示にします
            dialog.visibility = View.INVISIBLE

            // フラグを設定します
            isCallingAPi = false

        }

    }

    /** dataクラスの配列をViewmodelの配列に変換する関数です
     * @param lists ArrayList<EmotionsApiData> APIで取得したデータの配列
     */
    private fun listToViewModel (lists: ArrayList<FaceMusicApiData>): ArrayList<MusicViewModel> {

        var viewModels: ArrayList<MusicViewModel> = ArrayList(lists.size)

        for (list in lists) {

            var viewModel = MusicViewModel()
            viewModel.id = list.id
            viewModel.imageUrl = list.imageUrl
            viewModel.artist = list.artist
            viewModel.music = list.music
            viewModel.acousticness = list.acousticness
            viewModel.danceability = list.danceability
            viewModel.energy = list.energy
            viewModel.instrumentalness = list.instrumentalness
            viewModel.liveness = list.liveness
            viewModel.speechiness = list.speechiness
            viewModel.tempo = list.tempo
            viewModel.valence = list.valence
            viewModel.durationTime = list.durationTime
            viewModel.releaseDay = list.releaseDay

            viewModels.add(viewModel)

        }

        return viewModels
    }

    /** EC2インスタンスとの通信に失敗した場合に実行されるコールバック関数です **/

    override fun onFailure() {

        //メインスレッドで描画処理を行います
        val coroutine = CoroutineScope(Dispatchers.Main)

        coroutine.launch {

            // ダイアログを非表示にしまsu
            dialog.visibility = View.INVISIBLE

            // スナックバーを表示させます
            val rootLayout: View = findViewById(R.id.snackbar)
            Snackbar.make(rootLayout, "TEST", Snackbar.LENGTH_LONG).show()
            // フラグを設定します
            isCallingAPi = false


        }

    }

    override fun onClick(p0: View) {

        if (p0.id == R.id.music_snackbar) {

            // 画面遷移をします
            val intent = Intent(this, PlayMusicActivity::class.java)
            // 値を渡します
            intent.putExtra(Constants.KEY, Constants.SNACKBAR_CLICK)
            startActivity(intent)

        } else if (p0.id == R.id.back) {
            finish()
        }
    }


}