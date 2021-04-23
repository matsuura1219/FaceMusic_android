package com.example.facemusic.adapter

import android.R
import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.facemusic.data.MusicListItem
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.list_item.view.*


class MusicListAdapter: ArrayAdapter<MusicListItem> {

    /** 変数 */
    private var resource: Int = 0
    private var items: List<MusicListItem>? = null
    private var inflater: LayoutInflater? = null


    /** コンストラクタ */
    constructor (context: Context, resource: Int, items: List<MusicListItem>) : super(context, resource, items) {

        this.resource = resource
        this.items = items
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view: View

        if (convertView != null) {
            view = convertView
        } else {
            view = inflater!!.inflate(resource, null)
        }

        var item = items!!.get(position)

        //ジャケットを設定
        var photo: ImageView = view.photo
        photo.setImageBitmap(item.getPhoto())

        //アーティスト名を設定
        var artist = view.artist
        artist.setText(item.getArtist())

        //楽曲名を設定
        var music = view.music
        music.setText(item.getMusic())

        return view
    }

}