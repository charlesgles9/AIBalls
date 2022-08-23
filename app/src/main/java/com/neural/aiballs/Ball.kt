package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.RectF
import com.neural.aiballs.ai.NeuralNetwork
import com.neural.aiballs.algebra.Collision
import kotlin.math.*

class Ball(val originX: Float, val originY: Float, radius: Float,
           private val blocks:MutableList<Block>,private val checkpoints:MutableList<RectF>):Circle(
    originX,
    originY,
    radius
),Update {
    private var vision= mutableListOf<Ray>()
    val direction=Ray(originX, originY, originX + radius, originY)
    val lower=Ray(originX, originY, originX + radius, originY)
    val velocity=Vector2f(0f, 0f)
    //forward velocity
    val fv=7f
    val gravity=8f
    val momentum=15f
    var bounce=12f
    val mass=0.9f
    val friction=0.99f
    var angle=0.0f
    val input= mutableListOf<Double>()
    //3-> raycasts to the nearest edges, 4 -> upper and lower direction distance, velocityX and Y
    var network=NeuralNetwork(3+4+2,6,2)
    val score= mutableListOf<RectF>()
    val passed= mutableListOf<RectF>()
    var hitFloor=false
    init {
        direction.setColor(ColorRGBA.red)
        lower.setColor(ColorRGBA.cyan)
        val angles= floatArrayOf(180f,90f,-90f)
        for ( angle in angles){
            val ray=Ray(getX(),getY(),getX(),getY())
                ray.angle=angle
                ray.setColor(ColorRGBA.yellow)
            vision.add(ray)
        }
    }


    fun reset(){
        set(originX,originY)
        passed.clear()
    }
    override fun draw(batch: Batch) {
       batch.draw(this)
     /*  batch.draw(direction)
       batch.draw(lower)
       vision.forEach { ray->
           ray.draw(batch)
       }*/

    }



   private fun castRaysCollision(ray: Ray,startx:Float,starty:Float,stopx:Float,stopy:Float){
        val v=Collision.detect_line_collision(
            ray.getStartX(),
            ray.getStartY(),
            ray.getStopX(),
            ray.getStopY(),
            startx,
            starty,
            stopx,
            stopy)
       if (Collision.do_lines_intersect(v)) {
           Collision.setInterSectionPoint(v,ray)
       }

    }

    private fun predictNextMove(){
        for (ray in vision){
            input.add(ray.getDistance())
        }

        var nearest=checkpoints[0]

        checkpoints.forEach { check->
            if(Collision.distanceToQuad(this,nearest)>=Collision.distanceToQuad(this,check)&&
                    !score.contains(check)){
                nearest=check
                nearest.setColor(ColorRGBA.red)
            }

        }
        input.add(direction.getDistance())
        input.add(lower.getDistance())
        input.add(velocity.y.toDouble())
        input.add(velocity.x.toDouble())
        input.add(nearest.getX().toDouble())
        input.add(nearest.getY().toDouble())

        val output=network.predict(input)

        // move left or right
        velocity.x= fv*output[0].toFloat()


        // jump
        if(hitFloor){
            velocity.y=momentum*output[1].toFloat()
            bounce=velocity.y
            hitFloor=false
        }

        input.clear()
    }


    override fun update(delta: Long) {
        direction.set(getX(), getY(), getX() + 1000f * cos(angle), getY() + 1000f * sin(angle))
        lower.set(getX(), getY(), getX(), getY() + 1000f)
        var far=1000f
        vision.forEach { ray->
            ray.project(1000f,getX(),getY())
        }

        var dx=(velocity.y)* cos(angle)
        var dy=((velocity.y)* sin(angle))
        var g=gravity
        var vx=velocity.x
        for(block in blocks){

            for (line in block.lines){
                castRaysCollision(vision[0],line.getStartX(),line.getStartY(),line.getStopX(),line.getStopY())
                castRaysCollision(vision[1],line.getStartX(),line.getStartY(),line.getStopX(),line.getStopY())
                castRaysCollision(vision[2],line.getStartX(),line.getStartY(),line.getStopX(),line.getStopY())
                castRaysCollision(direction,line.getStartX(),line.getStartY(),line.getStopX(),line.getStopY())
                castRaysCollision(lower,line.getStartX(),line.getStartY(),line.getStopX(),line.getStopY())
            }

            //test collision
            if(block.collidesWith(this,dx,dy)){
                dx=0f
                dy=0f
            }
            if(block.collidesWith(this,vx,0f)){
                vx=0f
            }
            if(block.collidesWith(this,0f,g)){
                g=0f
            }
        }

        set(
            getX() + dx+vx,
            getY() + dy+g
        )

        if(velocity.y<=0.01f) velocity.y=0f
        predictNextMove()


    }

}