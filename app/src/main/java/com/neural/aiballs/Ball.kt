package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.maths.ColorRGBA
import com.graphics.glcanvas.engine.maths.Vector2f
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.Line

class Ball(val originX:Float,val originY:Float,radius:Float):Circle(originX,originY,radius),Update {
    private val direction=Line(originX,originY,originX+radius,originY)
    private val velocity=Vector2f()
    private val gravity=4f
    private val mass=0.9f
    private val angle=0f
    init {
        direction.setColor(ColorRGBA.red)
    }

    override fun draw(batch: Batch) {
       batch.draw(this)
       batch.draw(direction)
    }


    override fun update(delta: Long) {

        direction.set(getX(),getY(),getX()+getRadius(),getY())
    }
}