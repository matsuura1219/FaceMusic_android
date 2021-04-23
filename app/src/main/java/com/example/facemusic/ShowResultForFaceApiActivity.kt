package com.example.facemusic

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.utils.Easing
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing.EaseInOutQuad
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.*
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.back
import java.nio.channels.FileLock
import kotlin.math.IEEErem

/** Face APIの処理結果を表示する画面です **/

class ShowResultForFaceApiActivity : Activity() {

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

        next.setOnClickListener {

            //画面遷移を行います
            val intent = Intent(this, ShowMusicActivity::class.java)
            startActivity(intent)
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
}

