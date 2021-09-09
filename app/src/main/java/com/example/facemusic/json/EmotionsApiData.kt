package com.example.facemusic.json


/** EmotionsAPIを呼び出した後にjsonデータとして返却されるデータ群です **/

data class EmotionsApiData (val id: String, val imageUrl: String, val artist: String,
                            val music: String, val danceability: Float, val energy: Float,
                            val loudness: Float, val speechiness: Float, val acousticness: Float,
                            val instrumentalness: Float, val liveness: Float, val valence: Float, val tempo: Float)