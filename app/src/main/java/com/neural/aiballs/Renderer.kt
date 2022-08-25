package com.neural.aiballs

import android.content.Context
import android.opengl.GLES32
import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.BatchQueue
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
   private val checkpoints= mutableListOf<RectF>()
   private val blocks= mutableListOf<Block>()
   private var tmxMap= TmxParser(TmxLoader("ballTrack.tmx",context))
   private val population=60
   private val balls= mutableListOf<Ball>()
   private val timer=Timer(1000L)
   private var font:Font?=null
   private var timerLabel:GLLabel?=null
   private var generationLabel:GLLabel?=null
   private val timerLayout=RelativeLayoutConstraint(null,550f,80f)
   private val maxTime=45
    private var generation=1
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
                    "blocks" -> {
                        val block=Block(obj.x +obj.width*0.5f,obj.y +offsetY+obj.height*0.5f,obj.width,obj.height)
                        //check.setTexture(checkPointTexture!!)
                        blocks.add(block)
                    }
                    "checkpoints" -> {
                        val check=RectF(obj.x +obj.width*0.5f,obj.y +offsetY+obj.height*0.5f,obj.width,obj.height)
                        //check.setTexture(checkPointTexture!!)
                        checkpoints.add(check)

                    }
                    "start" -> {
                        camera.setPosition2D((obj.x),(obj.y))
                        val radius=25f
                        for(i in 0 until  population) {
                            balls.add(Ball(obj.x + radius, obj.y + radius, radius,blocks,checkpoints))
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

            timerLabel=GLLabel(250f,50f,font!!,"Time Left:",0.35f)
            generationLabel=GLLabel(150f,50f,font!!,"Gen:1",0.35f)
            timerLabel?.setTextColor(ColorRGBA.red)
            generationLabel?.setTextColor(ColorRGBA.green)
            generationLabel?.getConstraints()?.toRightOf(timerLabel!!)
            timerLayout.setBackgroundColor(ColorRGBA.transparent)
            timerLayout.addItem(timerLabel!!)
            timerLayout.addItem(generationLabel!!)
            timerLayout.set(getCanvasWidth()/2f,100f)



    }


    override fun draw() {
     GLES32.glClear(GLES32.GL_DEPTH_BUFFER_BIT or GLES32.GL_COLOR_BUFFER_BIT)
     GLES32.glClearColor(0f,0f,0f,1f)
        batch.setMode(BatchQueue.UNORDER)
        batch.begin(camera)
        balls.forEach { ball->
            ball.draw(batch)
        }
        blocks.forEach { block ->
          block.draw(batch)
        }
       /* checkpoints.forEach {
            batch.draw(it)
        }*/
        balls[balls.size-1].draw(batch)
        batch.end()

        batch.begin(cameraUI)
        timerLayout.draw(batch)
        batch.end()


      timerLabel?.setText("Time Left: "+(maxTime- timer.getTick()))

    }


    private fun geneticsAlgorithm(){
        generation++
        balls.sortBy { it.score.size }
        val children= mutableListOf<Ball>()
        // create children from the previous population
        for(i in 0 until balls.size/2){
            // pick a random parent
            val parent=balls[min(balls.size/2+Random.nextInt(balls.size/2),balls.size-1)]
            // create a child from the parent
            val child=Ball(parent.originX + parent.getRadius(),
                parent.originY + parent.getRadius(), parent.getRadius(),blocks,checkpoints)
            // copy the previous network data and apply a 1% mutation
            // no cross breeding
            child.network.copy(parent.network)
            NeuralNetwork.mutate(child.network,0.1f)
            children.add(child)
        }

        // then breed the top 3 balls
        for(i in balls.size-3 until balls.size){
            // pick a random parent
            val parent=balls[i]
            // create a child from the parent
            val child=Ball(parent.originX + parent.getRadius(),
                parent.originY + parent.getRadius(), parent.getRadius(),blocks,checkpoints)
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

    }

    override fun update(delta: Long) {


        balls.forEach { ball ->

             ball.update(delta)

            checkpoints.forEach { check->
                if(Collision.quadToCircleCollision(ball,check)&&!ball.score.contains(check))
                    ball.score.add(check)

            }
            ball.velocity.y*=ball.friction
            ball.bounce*=ball.mass
        }
        if(timer.getTick()>=maxTime) {
            timer.reset()
            geneticsAlgorithm()
        }


        timer.update(delta)

        generationLabel?.getTextView()?.setText("Gen: $generation")

        balls.sortBy { it.score.size }
        val best=balls[balls.size-1]
            best.network.start.set(getCanvasWidth()*0.7f,getCanvasHeight()*0.7f)
        best.network.initGraphics()

    }

    override fun onRelease() {
        super.onRelease()
        //clean up resources
        batch.cleanUp()
        TextureLoader.getInstance().clearTextures()
    }
}