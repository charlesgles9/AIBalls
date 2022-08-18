package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.structures.Circle

class PlatForm( x:Float, y:Float, width:Float, radius:Float):Update {

    private val edges= mutableListOf<Ray>()
    private val circles= mutableListOf<Circle>()
    init {
        edges.add(Ray(x-width/2,y,x+width/2,y+radius))
        edges.add(Ray(x-width/2,y,x+width/2,y-radius))
        circles.add(Circle(x-width/2,y,radius))
        circles.add(Circle(x+width/2,y,radius))
    }

    override fun draw(batch: Batch) {
        edges.forEach {edge->
            edge.draw(batch)
        }
        circles.forEach { circle->
            batch.draw(circle)
        }
    }

    override fun update(delta: Long) {

    }

}