package com.neural.aiballs.utils

import org.xmlpull.v1.XmlPullParser
import java.util.*

class TmxParser(private val loader: TmxLoader) {

     val data= Stack<TmxObjectGroup>()

    init {
        parse()
    }
    private fun parse(){
        var event=loader.xmlParser.eventType

        while (event!=XmlPullParser.END_DOCUMENT){
            val name=loader.xmlParser.name
            when(event){
                XmlPullParser.START_TAG->{
                    when(name){
                        "objectgroup"->
                            data.push(TmxObjectGroup(getAttributeName()))
                        "object"->{
                            val pair=getAttributeXY()
                            val obj=TmxObject(pair.first,pair.second)
                            val group=data.peek()
                            //if(group.name=="checkpoint"){
                                obj.width=getAttributeWidth()
                                obj.height=getAttributeHeight()
                            //}
                            group.addObject(obj)
                        }
                        "polygon"->
                            data.peek().getObjects().last().createTmxPolygon(getAttributePoints())
                    }
                }
                XmlPullParser.END_TAG->
                {

                }
            }
            event=loader.xmlParser.next()
        }
    }

    private fun getAttributeName():String{
        return loader.xmlParser.getAttributeValue(null,"name")
    }

    private fun getAttributeWidth():Float{
        val str= loader.xmlParser.getAttributeValue(null,"width") ?: return 0.0f
        return str.toFloat()
    }

    private  fun getAttributeHeight():Float{
        val str= loader.xmlParser.getAttributeValue(null,"height") ?: return 0.0f
        return str.toFloat()
    }
    private fun getAttributePoints():String{
        return loader.xmlParser.getAttributeValue(null,"points")
    }
    private fun getAttributeXY():Pair<Float,Float>{
        val x=loader.xmlParser.getAttributeValue(null,"x").toFloat()
        val y=loader.xmlParser.getAttributeValue(null,"y").toFloat()
        return Pair(x,y)
    }
}