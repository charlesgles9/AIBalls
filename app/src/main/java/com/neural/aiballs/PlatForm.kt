package com.neural.aiballs

import com.graphics.glcanvas.engine.Batch
import com.graphics.glcanvas.engine.Update
import com.graphics.glcanvas.engine.structures.Circle
import com.graphics.glcanvas.engine.structures.Line

class PlatForm(private val x:Float, private val y:Float,
               private val width:Float,
               private val radius:Float):Update {

    private val edges= mutableListOf<Ray>()
    private val circles= mutableListOf<Circle>()
    init {
        edges.add(Ray(x,y,x+radius,y))
        edges.add(Ray(x,y,x-radius,y))
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
        TODO("Not yet implemented")
    }




}