package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.Line
import kotlin.math.cos
import kotlin.math.sin

class Ball(val originX:Float,val originY:Float,radius:Float):Circle(originX,originY,radius),Update {
     val direction=Line(originX,originY,originX+radius,originY)
    val velocity=Vector2f()
    val gravity=8f
    var bounce=25f
    val mass=0.99f
    val friction=0.99f
    var angle=0.0f

    init {
        direction.setColor(ColorRGBA.red)
    }

    override fun draw(batch: Batch) {
       batch.draw(this)
       batch.draw(direction)

    }



    override fun update(delta: Long) {
        direction.set(getX(),getY(),getX()+ getRadius()* cos(angle),getY()+getRadius()* sin(angle))
        set(getX(),getY()+gravity)
        velocity.y*=friction
       bounce*=mass



    }
}