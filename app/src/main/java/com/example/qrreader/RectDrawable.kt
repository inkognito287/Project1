package com.example.qrreader

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode
import java.lang.Math.abs


class RectDrawable( context: Context,attributeSet: AttributeSet?): View(context,attributeSet) {
    private var faceBounds = RectF(96f,175f,195f,271f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var levelpaint= Paint()
        levelpaint.color = Color.BLUE
        levelpaint.style=Paint.Style.STROKE
        levelpaint.strokeWidth=1f
        var x=translationX
        var left= abs(translationX-faceBounds.left)
        var top=abs(translationY-faceBounds.top)
        var right=abs(translationX-faceBounds.right)
        var bottom=abs(translationY-faceBounds.bottom)
        faceBounds= RectF(left,top,right,bottom)
         canvas.drawRect(faceBounds,levelpaint)}

    fun drawFaceBounds(faceBounds: RectF){

        this.faceBounds = faceBounds
        invalidate()
    }

}