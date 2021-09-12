package com.example.facemusic.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.NumberPicker
import com.example.facemusic.R
import com.example.facemusic.`interface`.EC2ServerListener
import com.example.facemusic.application.MainApplication
import com.example.facemusic.const.Constants
import com.example.facemusic.json.FaceMusicApiData
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.service.EC2Client
import com.example.facemusic.util.CommonUtil
import com.example.facemusic.util.DialogUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.android.synthetic.main.activity_show_result_for_age.*
import kotlinx.android.synthetic.main.activity_show_result_for_age.back
import kotlinx.android.synthetic.main.activity_show_result_for_age.next
import kotlinx.android.synthetic.main.activity_show_result_for_age.overlay
import kotlinx.android.synthetic.main.activity_show_result_for_face_api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.util.Check

/** 年齢の判定結果を表示するActivityです **/

class ShowResultForAgeDetection : Activity(), View.OnClickListener, NumberPicker.OnValueChangeListener, EC2ServerListener {

    /** 定数 **/
    private val MIN_AGE: Int = 0
    private val MAX_AGE: Int = 100
    private val STANDARD_AGE = 25

    /** 変数 **/
    private var selectedAge: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result_for_age)

        //Viewの初期化を行います
        initComponents()

        // 初期化を行います
        init()

    }

    override fun onResume() {
        super.onResume()

    }


    /** Viewの初期化を行う関数です **/
    private fun initComponents () {

        // 0～100までをループさせる設定にします
        picker.wrapSelectorWheel = true
        // pickerの最大値・最小値を設定します
        picker.maxValue = MAX_AGE
        picker.minValue = MIN_AGE
        // 表示する値を設定します
        picker.value = MainApplication.getInstance().getUserInfo().getFaceApiData()?.faceAttributes?.age?.toInt() ?: STANDARD_AGE
        // 非表示にします
        picker.visibility = View.INVISIBLE

        // checkboxの初期表示を設定します
        checkbox.isChecked = false

        // 推定年齢を表示します
        age.text = MainApplication.getInstance().getUserInfo().getFaceApiData()?.faceAttributes?.age?.toInt()
            .toString()

    }

    /** 初期化を行う関数です **/
    private fun init () {

        selectedAge = MainApplication.getInstance().getUserInfo().getFaceApiData()?.faceAttributes?.age?.toInt() ?: Constants.STANDARD_AGE

        // リスナーを設定します
        back.setOnClickListener(this)
        next.setOnClickListener(this)
        picker.setOnValueChangedListener(this)
        checkbox.setOnClickListener(this)
    }



    /** ボタンクリック時に実行されるコールバック関数です **/
    override fun onClick(v: View) {

        if (v.id == R.id.back) {
            // 戻るボタンを押下した場合
            finish()
        } else if (v.id == R.id.next) {
            // NEXTボタンを押下した場合

            // オーバーレイを表示します
            overlay.visibility = View.VISIBLE

            // WebAPIをコールします

            var age: String = ""

            if (checkbox.isChecked) {
                // チェックがついている場合
                // 年齢はpickerに表示している値に設定します
                age = selectedAge.toString()

            } else {
                // チェックがついていない場合
                // 年齢は共通領域を設定します
                age = MainApplication.getInstance().getUserInfo().getFaceApiData()?.faceAttributes?.age?.toInt().toString()

            }

            EC2Client.getInstance().getMusicForAge(age, 0, Constants.DB_SEARCH_COUNT,this)


        } else if (v.id == R.id.checkbox) {
            // チェックボックスを押下した場合

            if (checkbox.isChecked) {
                // チェックをつけた場合
                // pickerを表示します
                picker.visibility = View.VISIBLE
            } else {
                // チェックを外した場合
                picker.visibility = View.INVISIBLE

            }
        }

    }

    /** pickerに表示されている値が変わった後に実行されるコールバック関数です
     * @param picker NumberPicker? picker
     * @param oldVal Int 切り替わる前の値
     * @param newVal Int 切り替わった後の値
     */
    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        selectedAge = newVal
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
            val intent = Intent(this@ShowResultForAgeDetection, ShowMusicActivity::class.java)
            startActivity(intent)

        }

    }

    /** EC2インスタンスとの通信に失敗した場合に実行されるコールバック関数です **/
    override fun onFailure() {

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
