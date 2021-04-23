package com.example.facemusic.data

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.facemusic.R

/** 取得した音楽をくらすです */

class MusicListItem {

    /** 変数 */
    //ジャケット写真
    private var photo: Bitmap? = null
    //アーティスト名
    private var artist: String = ""
    //曲名
    private var music: String = ""

    private var context: Context? = null

    /** コンストラクタ */
    constructor(context: Context, photo: Bitmap, artist: String, music: String) {
        this.context = context
        this.photo = photo
        this.artist = artist
        this.music = music
    }

    /** setter */
    fun setPhoto (photo: Bitmap) {
        this.photo = photo
    }

    fun setArtist (artist: String) {
        this.artist = artist
    }

    fun setMusic (music: String) {
        this.music = music
    }

    /** getter */
    fun getPhoto (): Bitmap {

        /*
        var resource: Resources = context!!.resources

        if (photo == null) {
            return BitmapFactory.decodeResource(resource, R.drawable.jacket)
        }
        */

        return  photo!!
    }

    fun getArtist (): String {
        return artist
    }

    fun getMusic (): String {
        return  music
    }

}