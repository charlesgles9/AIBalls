package com.neural.aiballs

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.structures.Circle
import com.neural.aiballs.algebra.Collision

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {

   private val batch=Batch()
   private val camera=Camera2D(10f)
   private val ball=Ball(250f,500f,50f)
    private val line4=Ray(100f,100f,100f,700f)
   private val line3=Ray(width-50f,100f,width-150f,700f)
   private val line2=Ray(0f,100f,width,200f)
   private val line1=Ray(0f,700f,width,700f)
   private val close=Ray(10f,10f,10f,10f)

    override fun prepare() {
      camera.setOrtho(getCanvasWidth(),getCanvasHeight())
      batch.initShader(context)
      line1.setColor(ColorRGBA.red)
    }

    override fun draw() {
     GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
     GLES32.glClearColor(0f,0f,0f,1f)
        batch.begin(camera)
        ball.draw(batch)
        batch.draw(line1)
        batch.draw(line2)
        batch.draw(line3)
        batch.draw(line4)
        batch.end()

    }


    override fun update(delta: Long) {
       ball.update(delta)
        Collision.circleToLineCollision(ball,line1,close)
        Collision.circleToLineCollision(ball,line2,close)
        Collision.circleToLineCollision(ball,line3,close)
        Collision.circleToLineCollision(ball,line4,close)
    }
}