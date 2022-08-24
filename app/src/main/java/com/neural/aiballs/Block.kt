package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.structures.RectF
import com.neural.aiballs.algebra.Collision
import kotlin.math.atan2

class Block(x:Float,y:Float,width:Float,height:Float):RectF(x,y,width, height),Update {


    val lines= mutableListOf<Ray>()

    init {

        val top=Ray(x-width*0.5f,y-height*0.5f,x+width*0.5f,y-height*0.5f)
        val bottom=Ray(x-width*0.5f,y+height*0.5f,x+width*0.5f,y+height*0.5f)
        val left=Ray(x-width*0.5f,y-height*0.5f,x-width*0.5f,y+height*0.5f)
        val right=Ray(x+width*0.5f,y-height*0.5f,x+width*0.5f,y+height*0.5f)
        lines.add(top)
        lines.add(bottom)
        lines.add(left)
        lines.add(right)
    }


    override fun draw(batch: Batch) {

        lines.forEach { line->
            line.draw(batch)
        }
    }


    override fun update(delta: Long) {
        super.update(delta)
    }


    fun collidesWith(ball: Ball,displacementX:Float,displacementY: Float):Boolean{
        // test if it collides with this edge and if true then bounce of the edge
        if(Collision.quadToCircleCollision(ball,this,displacementX,displacementY)){
            /*bounce of the edge of the line based on an imaginary line between the circle and the
            quad center position
             */
            for (ray in lines) {
                if(Collision.circleToLineCollision(
                    ball,
                    ray.getStartX(),
                    ray.getStartY(),
                    ray.getStopX(),
                    ray.getStopY(),
                    displacementX,
                    displacementY,
                    ball.direction

                )&&
                    // make sure it is the top ray
                    ray==lines[0]) {
                    ball.hitFloor = true
                }

            }


            return true
        }

        return false
    }


}