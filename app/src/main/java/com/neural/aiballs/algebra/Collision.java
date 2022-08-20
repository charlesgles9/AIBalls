package com.neural.aiballs.algebra;

import com.graphics.glcanvas.engine.maths.ColorRGBA;
import com.graphics.glcanvas.engine.structures.Circle;

import com.neural.aiballs.Ball;
import com.neural.aiballs.Ray;

public class Collision {




    public static boolean circleToLineCollision(Ball circle,float startx,float starty,float stopx,float stopy , Ray closest ){

        float line1x=stopx-startx;
        float line1y=stopy-starty;

        float line2x=circle.getX()-startx;
        float line2y=circle.getY()-starty;
        // get the length of the line
        float length=(float)line1x*line1x+line1y*line1y;

        //get dot product of the line
        float dot=Math.max(0f,Math.min(line1x*line2x+line1y*line2y,length))/length;

        //find the closes point on the line relative to the circle
        float closestX=startx+dot*(line1x);
        float closestY=starty+dot*(line1y);

        //closest distance to the center
        float dx=circle.getX()-closestX;
        float dy=circle.getY()-closestY;

        float distance=(float) (Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)));

        boolean collides=distance<=(circle.getRadius());
        if(collides){
            closest.set(circle.getX(),circle.getY(),closestX,closestY);
            circle.setAngle((float)Math.atan2(dy,dx));
            closest.setColor(ColorRGBA.Companion.getRed());
            float displacement=(1.0f+0.01f)-distance/circle.getRadius();
            circle.set(circle.getX()+(dx)*displacement,circle.getY()+(dy)*displacement);
            circle.getVelocity().set(0f,circle.getBounce());

        }else{


            closest.setColor(ColorRGBA.Companion.getGreen());
        }
        return collides;
    }

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
            float displacement=(1.0f)-distance/circle.getRadius();
            circle.set(circle.getX()+dx*displacement,circle.getY()+dy*displacement);
            circle.getVelocity().set(0f,circle.getBounce());

        }else{
            closest.setColor(ColorRGBA.Companion.getGreen());
        }
        return collides;
    }
}
