package com.neural.aiballs

import android.view.MotionEvent
import com.graphics.glcanvas.engine.Touch
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.ui.ScreenRatio

class CameraControl(private val listener:OnSwipeListener):Touch {

    private val start=Vector2f()
    private val end=Vector2f()
    private val diff=Vector2f()
    private var touched=false

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
              start.set(event.x,event.y)
              ScreenRatio.getInstance().project(start)

               touched=true
            }
            MotionEvent.ACTION_UP -> {
              start.set(0f,0f)
              end.set(0f,0f)
              touched=false
            }
            MotionEvent.ACTION_MOVE -> {
              end.set(event.x,event.y)
              ScreenRatio.getInstance().project(end)
                val x=(start.x-end.x)
                val y=(start.y-end.y)
                if(diff.x!=x&&diff.y!=y){
                    diff.x=x
                    diff.y=y
                    listener.onSwipe(diff)
                    start.set(end)
                }
            }
        }
        return touched
    }


    interface OnSwipeListener{

        fun onSwipe( vector:Vector2f)
    }


}