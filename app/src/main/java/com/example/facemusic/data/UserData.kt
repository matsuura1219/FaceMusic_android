package com.example.facemusic.data

import android.text.BoringLayout
import org.json.JSONObject
import java.util.function.BinaryOperator
import java.util.jar.JarOutputStream

/** ユーザの情報を保持するクラスです **/

class UserData {

    //static領域
    companion object {

        //インスタンス
        var _instance: UserData = UserData()
        //インスタンスを取得するメソッドです
        fun getInstance (): UserData {
            return _instance
        }

    }

    //faceID
    private var faceId: String = ""

    //faceRectangle
    private var faceRectangle: JSONObject = JSONObject()
    //-top
    private var top: String = ""
    //-left
    private var left: String = ""
    //-width
    private var width: String = ""
    //-height
    private var height: String = ""

    //faceAttribute
    private var faceAttribute: JSONObject = JSONObject()
    //-smile
    private var smile: String = ""
    //-headpose
    private var headpose: JSONObject = JSONObject()
    //--pitch
    private var pitch: String = ""
    //--roll
    private var roll: String = ""
    //--yaw
    private var yaw: String = ""

    //gender
    private var gender: String = ""

    //age
    private var age: String = ""

    //facialHair
    private var facialHair: JSONObject = JSONObject()
    //-moustache
    private var moustache: String = ""
    //-beard
    private var beard: String = ""
    //-sideburns
    private var sideburns: String = ""

    //glasses
    private var glasses: String = ""

    //emotion
    private var emotion: JSONObject = JSONObject()
    //-anger
    private var anger: String = ""
    //-contempt
    private var contempt: String = ""
    //-disgust
    private var disgust: String = ""
    //-fear
    private var fear: String = ""
    //-happiness
    private var happiness: String = ""
    //-neutral
    private var neutral: String = ""
    //-sadness
    private var saddness: String = ""
    //-surprise
    private var surprise: String = ""

    //blur
    private var blur: JSONObject = JSONObject()
    //-blurLevel
    private var blurLevel: String = ""
    //-value
    private var blur_value: String = ""

    //exposure
    private var exposure: JSONObject = JSONObject()
    //-exposureLevel
    private var exposureLevel: String = ""
    //-value
    private var exposure_value: String = ""

    //noise
    private var noise: JSONObject = JSONObject()
    //-noiseLevel
    private var noiseLevel: String = ""
    //-value
    private var noise_value: String = ""

    //makeup
    private var makeup: JSONObject = JSONObject()
    //-eyeMakeup
    private var eyeMakeup: Boolean = false
    //-lipMakeup
    private var lipMakeup: Boolean = false

    //accessories

    //occlusion
    private var occlusion: JSONObject = JSONObject()
    //-foreheadOccluded
    private var foreheadOccluded: Boolean = false
    //eyeOccluded
    private var eyeOccluded: Boolean = false
    //-mouthOccluded
    private var mouthOccluded: Boolean = false

    //hair
    private var hair: JSONObject = JSONObject()
    //-bald
    private var bald: String = ""
    //-invisible
    private var invisible: Boolean = false
    //-hairColor
    private var hairColor: JSONObject = JSONObject()
    //--color
    private var black: String = ""
    //--confidence
    private var black_confidence: String = ""
    //--color
    private var brown: String = ""
    //--confidence
    private var brown_confidence: String = ""
    //--color
    private var gray: String = ""
    //--confidence
    private var gray_confidence: String = ""
    //--color
    private var other: String = ""
    //--confidence
    private var other_confidence: String = ""
    //--color
    private var blond: String = ""
    //--confidence
    private var blond_confidence: String = ""
    //--color
    private var red: String = ""
    //--confidence
    private var red_confidence: String = ""
    //--color
    private var white: String = ""
    //--confidence
    private var white_confidence: String = ""


}