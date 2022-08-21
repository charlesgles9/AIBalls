package com.neural.aiballs

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView
import com.graphics.glcanvas.engine.maths.Vector2f

import com.graphics.glcanvas.engine.structures.PolyLine
import com.graphics.glcanvas.engine.utils.FpsCounter

import com.neural.aiballs.algebra.Collision
import com.neural.aiballs.utils.TmxLoader
import com.neural.aiballs.utils.TmxParser

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {

   private val batch=Batch()
   private val camera=Camera2D(10f)
   private val poly=PolyLine()
   private val close=Ray(10f,10f,10f,10f)
   private var tmxMap= TmxParser(TmxLoader("ballTrack.tmx",context))
   private val population=50
   private val balls= mutableListOf<Ball>()
    override fun prepare() {
      camera.setOrtho(getCanvasWidth(),getCanvasHeight())
      batch.initShader(context)
        //objectGroup
        tmxMap.data.forEach { group->
            //object
            for(obj in group.getObjects()){
                val offsetY = 50f
                //polygons
                if(group.name=="platforms") {
                    for (poly in obj.polygons) {
                        //points
                        for (j in 0 until poly.points.size - 1) {
                            val a = poly.points[j]
                            val b = poly.points[j + 1]
                            moveTo(obj.x + a.first, obj.y + a.second + offsetY)
                            lineTo(obj.x + b.first, obj.y + b.second + offsetY)

                        }
                        //join the last object with the first
                        val a = poly.points[poly.points.size - 1]
                        val b = poly.points[0]
                        moveTo(obj.x + a.first, obj.y + a.second + offsetY)
                        lineTo(obj.x + b.first, obj.y + b.second + offsetY)

                    }
                }else if(group.name=="checkpoint"){
                   // val check=RectF(obj.x +obj.width*0.5f,obj.y +offsetY+obj.height*0.5f,obj.width,obj.height)
                    //check.setTexture(checkPointTexture!!)
                    //checkpoints.add(check)

                }else if(group.name=="start"){
                    camera.setPosition2D((obj.x),(obj.y))
                    val radius=25f
                    for(i in 0 until  population) {
                        balls.add(Ball(obj.x + radius, obj.y + radius, radius, poly))
                    }

                }
            }

        }

        val cameraControl=CameraControl(object :CameraControl.OnSwipeListener{
            override fun onSwipe(vector: Vector2f) {
                camera.setPosition2D(camera.getX()+vector.x,
                 camera.getY()+vector.y)
            }
        })


        getController()?.addEvent(cameraControl)


    }
    private fun moveTo(x:Float,y:Float){
        poly.moveTo(x,y)
    }

    private fun lineTo(x:Float,y:Float){
        poly.lineTo(x,y)
    }

    override fun draw() {
     GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
     GLES32.glClearColor(0f,0f,0f,1f)
        batch.begin(camera)
        balls.forEach { ball->
            ball.draw(batch)
        }
        batch.draw(poly)
        batch.end()



    }


    override fun update(delta: Long) {

        balls.forEach { ball->
            ball.predictNextMove()
        }
       for(i in 0 until 1) {
           balls.forEach { ball->
               ball.update(delta)
           }

            poly.getPaths().forEach { path ->
                path.getEndPoints().forEach { end ->
                    balls.forEach { ball ->
                        Collision.circleToLineCollision(
                            ball, path.getStart().x, path.getStart().y,
                            end.x, end.y, close
                        )
                    }

               }
           }
       }

        balls.forEach { ball->
            ball.velocity.y*=ball.friction
        }
    }
}