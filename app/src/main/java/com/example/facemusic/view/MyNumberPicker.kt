package com.example.facemusic.view

import android.R.color
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import com.example.facemusic.R
import java.lang.reflect.Field


/**
 * NumberPickerクラスを継承したカスタムView
 * スクロールする度にレイアウトに対してViewが追加されているため、addViewメソッドで設定をアップデートする関数を処理する
 */

class MyNumberPicker(context: Context, attrs: AttributeSet) : NumberPicker(context, attrs) {

    private val _context = context

    /** Viewを追加するときに実行される関数です **/
    override fun addView(child: View?) {
        super.addView(child)
        updateView(child)
    }

    /** Viewを追加するときに実行される関数です **/
    override fun addView(child: View?, index: Int) {
        super.addView(child, index)
        updateView(child)

    }

    /** Viewを追加するときに実行される関数です **/
    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        updateView(child)
    }

    /** NumberPickerをカスタムする関数です **/
    private fun updateView(view: View?) {

        if (view is EditText) {

            val editText: EditText = view as EditText
            // 文字サイズ
            (editText).textSize = 18.0f
            // 文字色
            (editText).setTextColor(resources.getColor(R.color.white, null))

        }
    }

    /** NumberPickerの線の色を変更する関数です **/
    private fun changeColorToDividedLine(picker: NumberPicker, color: Int) {
        val pickerFields: Array<Field> = NumberPicker::class.java.declaredFields
        for (field in pickerFields) {
            if (field.name.equals("mSelectionDivider")) {
                field.isAccessible = true
                try {
                    val colorDrawable = ColorDrawable(resources.getColor(R.color.secondary, null))
                    field.set(this, colorDrawable)
                } catch (iae_argument: IllegalArgumentException) {
                    iae_argument.printStackTrace();
                } catch (nfe: Resources.NotFoundException) {
                    nfe.printStackTrace();
                } catch (iae_access: IllegalAccessException) {
                    iae_access.printStackTrace()
                }
                break
            }
        }
    }
}