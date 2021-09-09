package com.example.facemusic.json.faceapi

data class FaceApiData (val faceId: String, val faceRectangle: FaceRectangle, val faceAttributes: FaceAttribute)


/**
 *
 *
data class EmotionsAPIData (val id: String, val imageUrl: String, val artist: String,
val music: String, val danceability: Float, val energy: Float,
val loudness: Float, val speechiness: Float, val acousticness: Float,
val instrumentalness: Float, val liveness: Float, val valence: Float, val tempo: Float)

 */