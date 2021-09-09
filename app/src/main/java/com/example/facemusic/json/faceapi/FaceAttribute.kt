package com.example.facemusic.json.faceapi

data class FaceAttribute (val smile: String, val headPose: HeadPose,
                          val gender: String, val age: Double,
                          val facialHair: FacialHair, val glasses: String,
                          val emotion: Emotion, val blur: Blur,
                          val exposure: Exposure, val noise: Noise,
                          val makeup: MakeUp, val accessories: List<String>,
                          val occlusion: Occlusion, val hair: Hair
)