package com.neural.aiballs.algebra;

import com.graphics.glcanvas.engine.maths.ColorRGBA;
import com.graphics.glcanvas.engine.structures.Circle;

import com.neural.aiballs.Ball;
import com.neural.aiballs.Ray;

public class Collision {


    public static boolean circleToLineCollision(Ball circle, Ray line, Ray closest ){
        //get dot product of the line
        float dot=(float)(((circle.getX()-line.getStartX())*(line.getStopX()-line.getStartX())+
                (circle.getY()-line.getStartY())*(line.getStopY()-line.getStartY()))/Math.pow(line.getDistance(),2));

        //find the closes point on the line relative to the circle
        float closestX=line.getStartX()+dot*(line.getStopX()-line.getStartX());
        float closestY=line.getStartY()+dot*(line.getStopY()-line.getStartY());

        //closest distance to the center
        float dx=circle.getX()-closestX;
        float dy=circle.getY()-closestY;

        float distance=(float) (Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)));

        closest.set(circle.getX(),circle.getY(),closestX,closestY);


        boolean collides=distance<=circle.getRadius();
        if(collides){
            circle.setAngle((float)Math.atan2(dy,dx));
            closest.setColor(ColorRGBA.Companion.getRed());
            float displacement=(1.0f+0.01f)-distance/circle.getRadius();
            circle.set(circle.getX()+dx*displacement,circle.getY()+dy*displacement);
            circle.getVelocity().set(0f,circle.getBounce());

        }else{
            closest.setColor(ColorRGBA.Companion.getGreen());
        }
        return collides;
    }
}
