package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.PolyLine
import com.neural.aiballs.algebra.Collision
import kotlin.math.*

class Ball(val originX: Float, val originY: Float, radius: Float, val poly: PolyLine):Circle(
    originX,
    originY,
    radius
),Update {
    val direction=Ray(originX, originY, originX + radius, originY)
    val lower=Ray(originX, originY, originX + radius, originY)
    val velocity=Vector2f(5f, 0f)
    val gravity=9f
    var bounce=15f
    val mass=0.99999f
    val friction=0.99f
    var angle=0.0f
    init {
        direction.setColor(ColorRGBA.red)
        lower.setColor(ColorRGBA.cyan)
    }

    override fun draw(batch: Batch) {
       batch.draw(this)
       batch.draw(direction)
       batch.draw(lower)

    }

    override fun update(delta: Long) {
        direction.set(getX(), getY(), getX() + 1000f * cos(angle), getY() + 1000f * sin(angle))
        lower.set(getX(), getY(), getX(), getY() + 1000f)
        var far=1000f

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

            }
        }

        // test if the velocity vector is less than the distance to the next step
        var dx=(velocity.x+velocity.y)* cos(angle)
        var dy=((velocity.y)* sin(angle))
        val distance= max( sqrt((getX()-(getX()+dx)).pow(2.0f)+
                (getY()-(getY()+dy)).pow(2.0f)),5f)
        if(distance>direction.getDistance()){
            dx=abs(direction.getStartX()-direction.getStopX())* cos(angle)
            dy= max(abs(direction.getStartY()-direction.getStopY()),5f)* sin(angle)
        }
        /*test if the gravity distance is not greater than the distance between the ball and
        the next step. Test this separately to prevent the ball from randomly stopping in the air*/
        dy += if(lower.getDistance()<gravity){
            lower.getDistance().toFloat()
        }else{
            gravity
        }
        set(
            getX() + dx,
            getY() + dy
        )

        if(velocity.y<=0.01f) velocity.y=0f
          velocity.y*=friction
        bounce*=mass

    }
}