package com.example.facemusic.view

import android.view.animation.Animation
import android.view.animation.Transformation

class LineViewAnimation: Animation {

    private lateinit var view: LineView

    private var endPoint = 0

    constructor(view: LineView, endPoint: Int) {
        this.view = view
        this.endPoint = endPoint
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)

        val thisRate: Float = endPoint * interpolatedTime
        view.setEndPoint(thisRate)

        view.requestLayout()
    }
}