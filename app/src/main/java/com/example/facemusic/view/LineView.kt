package com.example.facemusic.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.facemusic.R

class LineView: View {

    private var endPoint: Float = 0.0f

    constructor(context: Context): super(context) {

    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {

    }

    override fun onDraw (canvas: Canvas) {
        super.onDraw(canvas)

        //初期表示
        //paint→鉛筆のようなもの
        val paint = Paint()
        //色を設定します
        paint.color = ContextCompat.getColor(context, R.color.white)
        //描画領域を設定します
        val rect = Rect(0, 0, 300, 300)
        //canvasに描画します
        canvas.drawRect(rect, paint)

        //アニメーション表示
        val paintAniamtion = Paint()
        paintAniamtion.color = ContextCompat.getColor(context, R.color.colorPrimary)
        val animationRect = Rect(0, 0, endPoint.toInt(), 300)
        canvas.drawRect(animationRect, paintAniamtion)

    }

    fun setEndPoint (end: Float) {
        endPoint = end

    }
}