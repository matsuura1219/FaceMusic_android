package com.example.facemusic.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.facemusic.`interface`.EC2ServerListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.CommonUtil
import com.example.facemusic.util.DialogUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_show_result_for_age.*
import kotlinx.android.synthetic.main.activity_show_result_for_age.back
import kotlinx.android.synthetic.main.activity_show_result_for_age.next
import kotlinx.android.synthetic.main.activity_show_result_for_age.overlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 年齢と性別の判定結果を表示するActivityです **/

/*
class ShowResultForAgeDetection : Activity(), EC2ServerListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result_for_age)

        //戻るアイコンを押下したときのイベントを設定します
        back.setOnClickListener {
            //現在のActivityを閉じ、前画面に戻ります
            finish()
        }

        //遷移元の値を受け取ります
        //前画面から感情データを受け取ります
        val intent = intent
        val age_data: Float = intent.getFloatExtra("age", 0.0f)
        val gender_data: String? = intent.getStringExtra("gender")
        val top_data: Int = intent.getIntExtra("top", 0)
        val left_data: Int = intent.getIntExtra("left", 0)
        val width_data: Int = intent.getIntExtra("width", 0)
        val height_data: Int = intent.getIntExtra("height", 0)

        var photo = CommonUtil.trimingPhoto(MainApplication.getInstance().getPhoto()!!, top_data, left_data, width_data, height_data)

        faceImg.setImageBitmap(photo)
        age.text = age_data.toString()

        next.setOnClickListener {

            overlay.visibility = View.VISIBLE

            //EC2インスタンスEmotionsAPIを呼び出します
            EC2ServerComm.getInstance().getMusicForAges(age_data, gender_data, this)

        }

    }

    override fun onSuccess(data: String?) {

        //jsonデータをパースします
        val mapper = jacksonObjectMapper()
        val jsonData = mapper.readValue<ArrayList<EmotionsAPIData>>(data!!)

        //val test = jsonData.get(0).loudness

        //共通領域に値を設定します
        val model = listToViewModel(jsonData)
        val myApp: MainApplication = MainApplication.getInstance()
        myApp.setMusicViewModel(model)

        //メインスレッドで描画処理を行います
        val coroutine = CoroutineScope(Dispatchers.Main)

        coroutine.launch {

            //オーバーレイを解除します
            overlay.visibility = View.INVISIBLE

            //画面遷移を行います
            val intent = Intent(this@ShowResultForAge, ShowMusicActivity::class.java)
            startActivity(intent)

        }

    }

    /** 配列をViewmodelに変換する関数です **/
    private fun listToViewModel (lists: ArrayList<EmotionsAPIData>): ArrayList<MusicViewModel> {

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

            viewModels.add(viewModel)

        }

        return viewModels
    }

    override fun onFailure() {

        DialogUtil.getInstance().showErrorMessage("通信に失敗しました", "はい", this, this)



    }

}
*/