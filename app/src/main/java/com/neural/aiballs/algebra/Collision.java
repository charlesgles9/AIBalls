package com.neural.aiballs.algebra;

import com.graphics.glcanvas.engine.maths.ColorRGBA;
import com.graphics.glcanvas.engine.structures.Circle;

import com.graphics.glcanvas.engine.structures.Line;
import com.graphics.glcanvas.engine.structures.RectF;
import com.neural.aiballs.Ball;
import com.neural.aiballs.Ray;

import kotlin.Pair;

public class Collision {



    public static boolean quadToCircleCollision(Ball circle, RectF rect){
        //get the distance between the two centers of the circle and the quad
        float dx=Math.abs(circle.getX()-rect.getX())-rect.getWidth()*0.5f;
        float dy=Math.abs(circle.getY()-rect.getY())-rect.getHeight()*0.5f;

        if(dx>circle.getRadius()||dy>circle.getRadius()) return false;
        if(dx<=0||dy<=0) return  true;
        // length between circle and the quad
        float length=(float)Math.sqrt(dx*dx+dy*dy);

        return length<=(circle.getRadius());
    }

    // test if the next step is safe
    public static boolean quadToCircleCollision(Ball circle, RectF rect,float displacementX,float displacementY){


        //get the distance between the two centers of the circle and the quad
        float dx=Math.abs(circle.getX()+displacementX-rect.getX())-rect.getWidth()*0.5f;
        float dy=Math.abs(circle.getY()+displacementY-rect.getY())-rect.getHeight()*0.5f;

        if(dx>circle.getRadius()||dy>circle.getRadius()) return false;
        if(dx<=0||dy<=0) return  true;
        // length between circle and the quad
        float length=(float)Math.sqrt(dx*dx+dy*dy);

        return length<=(circle.getRadius());
    }

    public static float distanceToQuad(Ball circle, RectF rect){
        //get the distance between the two centers of the circle and the quad
        float dx=circle.getX()-rect.getX();
        float dy=circle.getY()-rect.getY();
        // length between circle and the quad
        return (float)Math.sqrt(dx*dx+dy*dy);
    }

    public static Pair<Float,Float> circleToLineDistance(Ball circle,float startx,float starty,float stopx,float stopy ){
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
        return new Pair<>(closestX,closestY);
    }


    public static boolean circleToLineCollision(Ball circle,float startx,float starty,float stopx,float stopy ,float displacementX,float displacementY, Ray closest ){

        float line1x=stopx-startx;
        float line1y=stopy-starty;

        float line2x=circle.getX()+displacementX-startx;
        float line2y=circle.getY()+displacementY-starty;
        // get the length of the line
        float length=(float)line1x*line1x+line1y*line1y;

        //get dot product of the line
        float dot=Math.max(0f,Math.min(line1x*line2x+line1y*line2y,length))/length;

        //find the closes point on the line relative to the circle
        float closestX=startx+dot*(line1x);
        float closestY=starty+dot*(line1y);

        //closest distance to the center
        float dx=circle.getX()+displacementX-closestX;
        float dy=circle.getY()+displacementY-closestY;

        float distance=(float) (Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2)));

        boolean collides=distance<=(circle.getRadius());
        if(collides){
            closest.set(circle.getX(),circle.getY(),closestX,closestY);
            circle.setAngle((float)(Math.atan2(dy,dx)));
            closest.setColor(ColorRGBA.Companion.getRed());
           // float displacement=(1.0f+0.01f)-distance/circle.getRadius();
           // circle.set(circle.getX()+(dx)*displacement,circle.getY()+(dy)*displacement);
            circle.getVelocity().set(circle.getVelocity().getX(),circle.getGravity());

        }else{


            closest.setColor(ColorRGBA.Companion.getGreen());
        }
        return collides;
    }

    public static float detect_line_collision(float startAx,float startAy,float stopAx,float stopAy,
                                              float startBx,float startBy,float stopBx,float stopBy){
        float ua=0f;
        float ub;
        float ud=(stopBy-startBy)*(stopAx-startAx)-(stopBx-startBx)*(stopAy-startAy);


        if(ud!=0){
            ua=((stopBx-startBx)*(startAy-startBy)-(stopBy-startBy)*(startAx-startBx))/ud;
            ub=((stopAx-startAx)*(startAy-startBy)-(stopAy-startAy)*(startAx-startBx))/ud;

            if(ua<0.0f||ua>1.0f||ub<0.0f||ub>1.0f)ua=0.0f;
        }

        return ua;
    }

    public static boolean do_lines_intersect(Line a,Line b){
        return do_lines_intersect(detect_line_collision(a.getStartX(), a.getStartY(), a.getStopX(), a.getStopY(),
                b.getStartX(), b.getStartY(), b.getStopX(), b.getStopY()));
    }

    public static boolean do_lines_intersect(float startAx,float startAy,float stopAx,float stopAy,
                                             float startBx,float startBy,float stopBx,float stopBy){
        return do_lines_intersect(detect_line_collision(startAx, startAy, stopAx, stopAy, startBx, startBy, stopBx, stopBy));
    }

    public static boolean do_lines_intersect(float determinant){
        return determinant>0;
    }

    public static void setInterSectionPoint(float determinant, Ray line){
        float px1=line.getStartX();
        float px2=line.getStopX();
        float py1=line.getStartY();
        float py2=line.getStopY();
        line.setStopX(px1+determinant*(px2-px1));
        line.setStopY(py1+determinant*(py2-py1));
    }

}
