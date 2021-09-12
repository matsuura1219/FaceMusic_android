package com.example.facemusic.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.example.facemusic.R
import com.example.facemusic.`interface`.EC2ServerListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Constants
import com.example.facemusic.json.FaceMusicApiData
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.service.EC2Client
import com.example.facemusic.util.DialogUtil
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



class ShowResultForEmotionDetection: Activity(), View.OnClickListener, EC2ServerListener {

    /** 定数 **/

    // 進捗バーのアニメーションの時間
    private val ANIMATION_TIME: Long = 1000
    // プロパティ名
    private var PROPERTY_NAME = "progress"

    /** 変数 **/

    // 感情の値
    private var angerData: Float = 0.0f
    private var contemptData: Float = 0.0f
    private var disgustData: Float = 0.0f
    private var fearData: Float = 0.0f
    private var happinessData: Float = 0.0f
    private var neutralData: Float = 0.0f
    private var sadnessData: Float = 0.0f
    private var surpriseData: Float = 0.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result_for_face_api)

        // 初期化を行います
        init()

        // 感情データを百分率にします
        val emotion = MainApplication.getInstance().getUserInfo().getFaceApiData()!!.faceAttributes.emotion
        angerData = emotion.anger.toFloat() * 100
        contemptData = emotion.contempt.toFloat() * 100
        disgustData = emotion.disgust.toFloat() * 100
        fearData = emotion.fear.toFloat() * 100
        happinessData = emotion.happiness.toFloat() * 100
        neutralData = emotion.neutral.toFloat() * 100
        sadnessData = emotion.sadness.toFloat() * 100
        surpriseData = emotion.surprise.toFloat() * 100

        // アニメーションを実行します
        doAnimation(angerData, contemptData, disgustData, fearData, happinessData, neutralData, sadnessData, surpriseData)

    }

    /** 初期化を行う関数です **/

    private fun init () {

        // イベント定義
        back.setOnClickListener(this)
        next.setOnClickListener(this)
    }


    /**
     * 進捗バーをアニメーション表示する関数です
     * @param anger_data Float 怒り
     * @param contempt_data Float 軽蔑
     * @param disgust_data Float 嫌悪
     * @param fear_data Float 恐怖
     * @param happiness_data FLoat 幸福
     * @param neutral_data Float 自然
     * @param sadness_data Float 悲しさ
     * @param surprise_data Float 驚き
     */
    private fun doAnimation (anger_data: Float, contempt_data: Float, disgust_data: Float, fear_data: Float, happiness_data: Float, neutral_data: Float, sadness_data: Float, surprise_data: Float) {

        val anger_animation = ObjectAnimator.ofInt(anger, PROPERTY_NAME, anger_data.toInt())
        anger_animation.duration = ANIMATION_TIME
        anger_animation.interpolator = DecelerateInterpolator()
        anger_animation.start()

        val contempt_animation = ObjectAnimator.ofInt(contempt, PROPERTY_NAME, contempt_data.toInt())
        contempt_animation.duration =ANIMATION_TIME
        contempt_animation.interpolator = DecelerateInterpolator()
        contempt_animation.start()

        val disgust_animation = ObjectAnimator.ofInt(disgust, PROPERTY_NAME, disgust_data.toInt())
        disgust_animation.duration = ANIMATION_TIME
        disgust_animation.interpolator = DecelerateInterpolator()
        disgust_animation.start()

        val fear_animation = ObjectAnimator.ofInt(fear, PROPERTY_NAME, fear_data.toInt())
        fear_animation.duration = ANIMATION_TIME
        fear_animation.interpolator = DecelerateInterpolator()
        fear_animation.start()

        val happiness_animation = ObjectAnimator.ofInt(happiness, PROPERTY_NAME, happiness_data.toInt())
        happiness_animation.duration = ANIMATION_TIME
        happiness_animation.interpolator = DecelerateInterpolator()
        happiness_animation.start()

        val neutral_animation = ObjectAnimator.ofInt(neutral, PROPERTY_NAME, neutral_data.toInt())
        neutral_animation.duration = ANIMATION_TIME
        neutral_animation.interpolator = DecelerateInterpolator()
        neutral_animation.start()

        val sadness_animation = ObjectAnimator.ofInt(sadness, PROPERTY_NAME, sadness_data.toInt())
        sadness_animation.duration = ANIMATION_TIME
        sadness_animation.interpolator = DecelerateInterpolator()
        sadness_animation.start()

        val surprise_animation = ObjectAnimator.ofInt(surprise, PROPERTY_NAME, surprise_data.toInt())
        surprise_animation.duration = ANIMATION_TIME
        surprise_animation.interpolator = DecelerateInterpolator()
        surprise_animation.start()

    }

    /** クリックイベント後に実行されるコールバック関数です
     * @param p0 View? フォーカスが当たっているViewコンポーネント
     */
    override fun onClick(p0: View?) {

        when (p0!!.id) {

            back.id -> {

                finish()
            }

            next.id -> {

                // オーバーレイを表示します
                overlay.visibility = View.VISIBLE

                // EC2インスタンスEmotionsAPIを呼び出します
                EC2Client.getInstance().getMusicForEmtion(angerData, contemptData, disgustData, fearData, happinessData, neutralData, sadnessData, surpriseData, 0, Constants.DB_SEARCH_COUNT,this)
            }
        }
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
        val myApp: MainApplication = MainApplication.getInstance()
        myApp.setMusicViewModel(model)

        //メインスレッドで描画処理を行います
        val coroutine = CoroutineScope(Dispatchers.Main)

        coroutine.launch {

            // オーバーレイを解除します
            overlay.visibility = View.INVISIBLE

            // 画面遷移を行います
            val intent = Intent(this@ShowResultForEmotionDetection, ShowMusicActivity::class.java)
            startActivity(intent)

        }

    }

    /** EC2インスタンスとの通信に失敗、もしくはjsonデータを正常に取得できなかった場合に実行されるコールバック関数です **/
    override fun onFailure() {

        //ダイアログを表示します
        DialogUtil.getInstance().showErrorMessage(getString(R.string.not_connect_network_ec2), getString(R.string.yes), this)

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



}