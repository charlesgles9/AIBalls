package com.neural.aiballs

import android.content.Context
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {

   private val batch=Batch()
   private val camera=Camera2D(10f)

    override fun prepare() {
      camera.setOrtho(getCanvasWidth(),getCanvasHeight())
      batch.initShader(context)
    }


    override fun draw() {

    }


    override fun update(delta: Long) {

    }
}