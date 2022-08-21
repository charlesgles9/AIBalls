package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.Line
import com.graphics.glcanvas.engine.structures.PolyLine
import com.neural.aiballs.ai.NeuralNetwork
import com.neural.aiballs.algebra.Collision
import kotlin.math.*

class Ball(val originX: Float, val originY: Float, radius: Float, val poly: PolyLine):Circle(
    originX,
    originY,
    radius
),Update {
    private var vision= mutableListOf<Ray>()
    val direction=Ray(originX, originY, originX + radius, originY)
    val lower=Ray(originX, originY, originX + radius, originY)
    val velocity=Vector2f(0f, 0f)
    //forward velocity
    val fv=6f
    val gravity=8f
    var bounce=14f
    val friction=0.99f
    var angle=0.0f
    val input= mutableListOf<Double>()
    var network=NeuralNetwork(3+4,6,2)
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

    override fun draw(batch: Batch) {
       batch.draw(this)
      // batch.draw(direction)
     //  batch.draw(lower)
       /*vision.forEach { ray->
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

    fun predictNextMove(){
        for (ray in vision){
            input.add(ray.getDistance())
        }

        input.add(direction.getDistance())
        input.add(lower.getDistance())
        input.add(velocity.y.toDouble())
        input.add(velocity.x.toDouble())
        val output=network.predict(input)

        // move left or right
        velocity.x= fv*output[0].toFloat()*2f-fv

        // jump
        if(lower.getDistance()<=getRadius()){
            velocity.y=bounce*output[1].toFloat()
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
        // get the closest distance from the ball to the an edge
        poly.getPaths().forEach { path ->
            path.getEndPoints().forEach { end ->

                val d1=Collision.circleToLineDistance(
                    this, path.getStart().x, path.getStart().y,
                    end.x, end.y
                )
                //closest distance to the center
                val dx1 = getX() - d1.first
                val dy1 = getY() - d1.second

                val distance1 = sqrt(dx1.toDouble().pow(2.0) + dy1.toDouble().pow(2.0))
                        .toFloat()
                if(distance1<far){
                     far=distance1
                    direction.setStopX(d1.first)
                    direction.setStopY(d1.second)
                }

              val d2=  Collision.detect_line_collision(lower.getStartX(),lower.getStartY()
                    ,lower.getStopX(),lower.getStopY(),path.getStart().x, path.getStart().y,
                    end.x, end.y)

               if(Collision.do_lines_intersect(d2)){
                   Collision.setInterSectionPoint(d2,lower)
               }

                castRaysCollision(vision[0],path.getStart().x, path.getStart().y, end.x, end.y)
                castRaysCollision(vision[1],path.getStart().x, path.getStart().y, end.x, end.y)
                castRaysCollision(vision[2],path.getStart().x, path.getStart().y, end.x, end.y)

            }
        }


        val dx=velocity.x+(velocity.y)* cos(angle)
        val dy=(gravity+(velocity.y)* sin(angle))

        set(
            getX() + dx,
            getY() + dy
        )

        if(velocity.y<=0.01f) velocity.y=0f




    }
}