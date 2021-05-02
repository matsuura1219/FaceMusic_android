package com.example.facemusic.adapter

import android.R
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.facemusic.data.MusicListItem
import com.example.facemusic.model.MusicViewModel
import com.example.facemusic.util.CommonUtil
import com.example.facemusic.util.FaceApiUtil
import com.example.facemusic.util.S3Util
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MusicListAdapter: ArrayAdapter<MusicViewModel> {

    /** 変数 */
    private var resource: Int = 0
    private var items: List<MusicViewModel>? = null
    private var inflater: LayoutInflater? = null


    /** コンストラクタ */
    constructor (context: Context, resource: Int, items: List<MusicViewModel>) : super(context, resource, items) {

        this.resource = resource
        this.items = items
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view: View

        //描画するviewを変数に設定します
        if (convertView != null) {
            view = convertView
        } else {
            view = inflater!!.inflate(resource, null)
        }

        //listviewのposition番目にあるitemsを変数に設定します
        var item = items!![position]

        //ジャケットを設定
        var photo: WebView = view.photo
        photo.settings.useWideViewPort = true;
        photo.settings.loadWithOverviewMode = true;
        photo.loadUrl(item.imageUrl)

        //アーティスト名を設定
        var artist = view.artist
        artist.text = item.artist

        //楽曲名を設定
        var music = view.music
        music.text = item.music

        return view
    }

}