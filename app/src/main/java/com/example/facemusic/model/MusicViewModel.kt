package com.example.facemusic.model

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class MusicViewModel: ViewModel() {

    //楽曲id
    var id: String = ""
    //ジャケット写真
    var imageUrl: String = ""
    //アーティスト名
    var artist: String = ""
    //楽曲名
    var music: String = ""
    //danceablity
    var danceability: Float = 0f
    //energy
    var energy: Float = 0f
    //loudness
    var loudness: Float = 0f
    //speechiness
    var speechiness: Float = 0f
    //acousticness
    var acousticness: Float = 0f
    //instrumentalness
    var instrumentalness: Float = 0f
    //liveness
    var liveness: Float = 0f
    //valence
    var valence: Float = 0f
    //tempo
    var tempo: Float = 0f
    
}