package com.example.facemusic

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.example.facemusic.`interface`.EC2ServerListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.json.EmotionsAPIData
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.EC2ServerComm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.*
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.back
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.next
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.overlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Face APIの処理結果を表示する画面です **/

class ShowResultForFaceApiActivity : Activity(), EC2ServerListener {

    /** 定数 **/

    //進捗バーのアニメーションの時間
    private val animationTime: Long = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result_for_face_api)

        //戻るアイコンを押下したときのイベントを設定します
        back.setOnClickListener {
            //現在のActivityを閉じ、前画面に戻ります
            finish()
        }

        //前画面から感情データを受け取ります
        val intent = intent
        val anger_data: Float = intent.getFloatExtra("anger", 0.0f) * 100
        val contempt_data: Float = intent.getFloatExtra("contempt", 0.0f) * 100
        val disgust_data: Float = intent.getFloatExtra("disgust", 0.0f) * 100
        val fear_data: Float = intent.getFloatExtra("fear", 0.0f) * 100
        val happiness_data: Float = intent.getFloatExtra("happiness", 0.0f) * 100
        val neutral_data: Float = intent.getFloatExtra("neutral", 0.0f) * 100
        val sadness_data: Float = intent.getFloatExtra("sadness", 0.0f) * 100
        val surprise_data: Float = intent.getFloatExtra("surprise", 0.0f) * 100

        doAnimation(anger_data, contempt_data, disgust_data, fear_data, happiness_data, neutral_data, sadness_data, surprise_data)

        next.setOnClickListener {

            //オーバーレイを表示します
            overlay.visibility = View.VISIBLE

            //EC2インスタンスEmotionsAPIを呼び出します
            EC2ServerComm.getInstance().getMusicForEmtions(anger_data, contempt_data, disgust_data, fear_data, happiness_data, neutral_data, sadness_data, surprise_data, this)

        }

    }

    /**
     * 進捗バーをアニメーション表示する関数です
     * */
    private fun doAnimation (anger_data: Float, contempt_data: Float, disgust_data: Float, fear_data: Float, happiness_data: Float, neutral_data: Float, sadness_data: Float, surprise_data: Float) {

        val anger_animation = ObjectAnimator.ofInt(anger, "progress", anger_data.toInt())
        anger_animation.duration = animationTime
        anger_animation.interpolator = DecelerateInterpolator()
        anger_animation.start()

        val contempt_animation = ObjectAnimator.ofInt(contempt, "progress", contempt_data.toInt())
        contempt_animation.duration = animationTime
        contempt_animation.interpolator = DecelerateInterpolator()
        contempt_animation.start()

        val disgust_animation = ObjectAnimator.ofInt(disgust, "progress", disgust_data.toInt())
        disgust_animation.duration = animationTime
        disgust_animation.interpolator = DecelerateInterpolator()
        disgust_animation.start()

        val fear_animation = ObjectAnimator.ofInt(fear, "progress", fear_data.toInt())
        fear_animation.duration = animationTime
        fear_animation.interpolator = DecelerateInterpolator()
        fear_animation.start()

        val happiness_animation = ObjectAnimator.ofInt(happiness, "progress", happiness_data.toInt())
        happiness_animation.duration = animationTime
        happiness_animation.interpolator = DecelerateInterpolator()
        happiness_animation.start()

        val neutral_animation = ObjectAnimator.ofInt(neutral, "progress", neutral_data.toInt())
        neutral_animation.duration = animationTime
        neutral_animation.interpolator = DecelerateInterpolator()
        neutral_animation.start()

        val sadness_animation = ObjectAnimator.ofInt(sadness, "progress", sadness_data.toInt())
        sadness_animation.duration = animationTime
        sadness_animation.interpolator = DecelerateInterpolator()
        sadness_animation.start()

        val surprise_animation = ObjectAnimator.ofInt(surprise, "progress", surprise_data.toInt())
        surprise_animation.duration = animationTime
        surprise_animation.interpolator = DecelerateInterpolator()
        surprise_animation.start()

    }

    /** EC2インスタンスとの通信に成功し、jsonデータを正常に取得できた場合に実行されるコールバック関数です **/
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
            val intent = Intent(this@ShowResultForFaceApiActivity, ShowMusicActivity::class.java)
            startActivity(intent)

        }

    }

    /** EC2インスタンスとの通信に失敗、もしくはjsonデータを正常に取得できなかった場合に実行されるコールバック関数です **/
    override fun onFailure() {

        //ダイアログを表示します

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
}

