package com.worho.shoppinglist.utils

import android.view.MotionEvent
import android.view.View

class MyTouchListener: View.OnTouchListener {
    var xDelta = 0.0f
    var yDelta = 0.0f
    override fun onTouch(p0: View, event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                xDelta = p0.x - event.rawX
                yDelta = p0.y - event.rawY
            }
            MotionEvent.ACTION_MOVE ->{
                p0.x = xDelta + event.rawX
                p0.y = yDelta + event.rawY
            }
        }
        return true
    }
}