package com.neural.aiballs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.graphics.glcanvas.engine.GLCanvasSurfaceView

class MainActivity : AppCompatActivity() {
    private var surface:GLCanvasSurfaceView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surface=GLCanvasSurfaceView(this,Renderer(this,720f,1280f))
        setContentView(surface)
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        surface?.onRelease()
    }
}