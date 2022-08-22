package com.neural.aiballs

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Camera2D
import com.graphics.glcanvas.engine.GLRendererView
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Font

import com.graphics.glcanvas.engine.structures.PolyLine
import com.graphics.glcanvas.engine.structures.RectF
import com.graphics.glcanvas.engine.ui.GLLabel
import com.graphics.glcanvas.engine.ui.RelativeLayoutConstraint
import com.graphics.glcanvas.engine.utils.FpsCounter
import com.graphics.glcanvas.engine.utils.TextureLoader
import com.neural.aiballs.ai.NeuralNetwork

import com.neural.aiballs.algebra.Collision
import com.neural.aiballs.utils.Timer
import com.neural.aiballs.utils.TmxLoader
import com.neural.aiballs.utils.TmxParser
import kotlin.math.min
import kotlin.random.Random

class Renderer(private val context: Context,width:Float,height:Float):GLRendererView(width, height) {

   private val batch=Batch()
   private val cameraUI=Camera2D(10f)
   private val camera=Camera2D(10f)
   private val poly=PolyLine()
   private val checkpoints= mutableListOf<RectF>()
   private val close=Ray(10f,10f,10f,10f)
   private var tmxMap= TmxParser(TmxLoader("ballTrack.tmx",context))
   private val population=1
   private val balls= mutableListOf<Ball>()
   private val timer=Timer(1000L)
   private var font:Font?=null
   private var timerLabel:GLLabel?=null
   private val timerLayout=RelativeLayoutConstraint(null,250f,80f)
   private val maxTime=30
    override fun prepare() {
      camera.setOrtho(getCanvasWidth(),getCanvasHeight())
      cameraUI.setOrtho(getCanvasWidth(),getCanvasHeight())
      batch.initShader(context)
      font= Font("fonts/arial.fnt",context)
        //objectGroup
        tmxMap.data.forEach { group->
            //object
            for(obj in group.getObjects()){
                val offsetY = 50f
                //polygons
                when (group.name) {
                    "platforms" -> {
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
                    }
                    "checkpoint" -> {
                        val check=RectF(obj.x +obj.width*0.5f,obj.y +offsetY+obj.height*0.5f,obj.width,obj.height)
                        //check.setTexture(checkPointTexture!!)
                        checkpoints.add(check)

                    }
                    "start" -> {
                        camera.setPosition2D((obj.x),(obj.y))
                        val radius=25f
                        for(i in 0 until  population) {
                            balls.add(Ball(obj.x + radius, obj.y + radius, radius, poly,checkpoints))
                        }

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

            timerLabel=GLLabel(150f,50f,font!!,"Time Left:",0.2f)
            timerLabel?.setTextColor(ColorRGBA.red)
            timerLayout.setBackgroundColor(ColorRGBA.transparent)
            timerLayout.addItem(timerLabel!!)
            timerLayout.set(getCanvasWidth()/2f,100f)


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

        batch.begin(cameraUI)
        timerLayout.draw(batch)
        batch.end()


      timerLabel?.setText("Time Left: "+(maxTime- timer.getTick()))
    }


    private fun geneticsAlgorithm(){

        balls.sortBy { it.score.size }
        val children= mutableListOf<Ball>()
        // create children from the previous population
        for(i in 0 until balls.size/2){
            // pick a random parent
            val parent=balls[min(balls.size/2+Random.nextInt(balls.size/2),balls.size-1)]
            // create a child from the parent
            val child=Ball(parent.originX + parent.getRadius(),
                parent.originY + parent.getRadius(), parent.getRadius(), poly,checkpoints)
            // copy the previous network data and apply a 1% mutation
            // no cross breeding
            child.network.copy(parent.network)
            NeuralNetwork.mutate(child.network,0.1f)
            children.add(child)
        }

        // replace 50% the previous population with a new children
        for(i in 0 until children.size){
            balls[i]=children[i]
        }

        //reset the position of the ball to the original start position
        for (i in 0 until  balls.size){
            balls[i].reset()
        }
        children.clear()
    }

    override fun update(delta: Long) {

        if(timer.getTick()>=maxTime) {
            timer.reset()
            geneticsAlgorithm()
        }

        balls.forEach { ball->
            ball.predictNextMove()
            checkpoints.forEach {check->
                if(Collision.quadToCircleCollision(ball,check)&&!ball.score.contains(check)){
                    ball.score.add(check)
                    checkpoints.add(check)
                }
            }
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

        timer.update(delta)
    }

    override fun onRelease() {
        super.onRelease()
        //clean up resources
        batch.cleanUp()
        TextureLoader.getInstance().clearTextures()
    }
}