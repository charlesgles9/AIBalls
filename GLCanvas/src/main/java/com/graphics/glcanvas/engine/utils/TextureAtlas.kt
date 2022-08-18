package com.graphics.glcanvas.engine.utils

import android.content.Context
import com.graphics.glcanvas.engine.maths.Vector2f
import java.lang.StringBuilder


 class TextureAtlas(text: StringBuilder,context: Context) {
    private var texturePath=""
    private var texture:Texture?=null
    private var resolution=Vector2f()
    private var format=""
    private val map=HashMap<String,ArrayList<AtlasMetaData>>()
    private var sheet:SpriteSheet?=null
    private var current:ArrayList<AtlasMetaData>?=null
    init {
        parse(text)
        sheet= SpriteSheet(1,1)
        sheet?.resize(coordinateCount())
        var counter=0

        map.forEach{(k,l)->
           l.sortBy {
               it.getIndex()
           }
            l.forEach { v->
                 sheet?.setSTMatrix(
                     v.getPosition().x, v.getPosition().y,
                     v.getSize().x, v.getSize().y,
                     resolution.x, resolution.y, counter
                 )
                 v.setSheetIndex(counter)
                 counter++
             }
        }
       texture= Texture(context,texturePath)
    }


    private fun coordinateCount():Int{
        var counter=0
        map.forEach{(k,l)->
            counter+=l.size
        }
        return counter
    }
    private fun split(text:String):List<String>{
        return text.split(":")
    }

    private fun get(name:String):ArrayList<AtlasMetaData>?{
         return map[name]
    }

    private fun get(name:String, index: Int):AtlasMetaData?{
        val list=get(name)
        if(index>list?.size?:0)
            return list?.get(0)
        return list?.get(index)
    }

    private fun parse(text: StringBuilder){
        val list=text.split("\n")
        list.forEach {
            // if it doesn't contain this sign then this line is a title
             if(!it.contains(":")){
                 // test if this data is the image details located at the
                 // start of the text file
                 if (map.isEmpty()&&texturePath=="") {
                     texturePath = it
                 }else {
                     val objList=get(it)
                     if(objList==null){
                      current= ArrayList()
                      current?.add(AtlasMetaData(it))
                      map[it] = current!!
                      }else {
                         current = objList
                         current?.add(AtlasMetaData(it))
                     }
                 }
             }else{
                 // sprite coordinate data and texture information
                 if(map.isEmpty()) {
                     when {
                         it.indexOf("size") != -1->{
                             val arr = split(it)[1].split(',')
                             resolution.set(arr[0].trim().toFloat(), arr[1].trim().toFloat())
                         }

                         it.indexOf("format") != -1 -> {
                             val format = split(it)[1].trim()
                             val obj = current?.last()
                             obj?.setFormat(format)
                         }
                         it.indexOf("filter") != -1 -> {
                             val filter = split(it)[1].split(',')
                             val obj = current?.last()
                             obj?.setFilter(filter[0].trim(), filter[1].trim())
                         }
                     }

                 }else{

                     when {
                         it.indexOf("size") != -1->{
                             val size = split(it)[1].split(',')
                             val obj=current?.last()
                                 obj?.setSize(size[0].trim().toFloat(), size[1].trim().toFloat())
                         }
                         it.indexOf("rotate") != -1 -> {
                             val rotate = split(it)[1].trim()
                             val obj=current?.last()
                             obj?.setRotate(rotate.toBoolean())
                         }
                         it.indexOf("xy") != -1 -> {
                             val xy = split(it)[1].split(",")
                             val obj=current?.last()
                             obj?.setPosition(xy[0].trim().toFloat(), xy[1].trim().toFloat())
                         }
                         it.indexOf("orig") != -1 -> {
                             val origin = split(it)[1].split(",")
                             val obj=current?.last()
                               obj?.setOrigin(
                                 origin[0].trim().toFloat(),
                                 origin[1].trim().toFloat()
                             )
                         }


                         it.indexOf("offset") != -1 -> {
                             val offset = split(it)[1].split(",")
                             val obj=current?.last()
                                obj?.setOffset(
                                 offset[0].trim().toFloat(),
                                 offset[1].trim().toFloat()
                             )
                         }
                         it.indexOf("index") != -1 -> {
                             val index = split(it)[1].trim()
                             val obj=current?.last()
                             obj?.setIndex(index.toInt())
                         }
                     }
                 }
             }
        }
    }


    fun getItem(key:String):AtlasMetaData?{
        return map[key]?.get(0)
    }

    fun getSheet():SpriteSheet?{
        return sheet
    }

    fun getTextureCoordinate(key: String):Int{
        return get(key,0)?.getSheetIndex()?:0
    }
    fun getTextureCoordinate(key: String,index: Int):Int{
        return get(key,index)?.getSheetIndex()?:0
    }
    fun getTexture():Texture?{
        return texture
    }

    inner class AtlasMetaData(private val name:String){
        private var rotate=false
        private var position=Vector2f()
        private var format="RGBA8888"
        private var filter=Array(2,init = {"nearest"})
        private var size= Vector2f()
        private var origin=Vector2f()
        private var offset=Vector2f()
        private var index=-1
        private var sheetIndex=0
        override fun hashCode(): Int {
            return super.hashCode()
        }
        fun setRotate(rotate: Boolean){
            this.rotate=rotate
        }
        fun setPosition(x:Float,y:Float){
            this.position.set(x,y)
        }
        fun setSize(x:Float,y:Float){
            this.size.set(x,y)
        }
        fun setOrigin(x:Float,y:Float){
            this.origin.set(x,y)
        }
        fun setOffset(x:Float,y:Float){
            this.offset.set(x,y)
        }

        fun setFormat(format:String){
           this.format=format
        }

        fun setFilter(f1:String,f2:String){
            this.filter[0]=f1
            this.filter[1]=f2
        }

        fun setIndex(index:Int){
            this.index=index
        }

        fun setSheetIndex(sheetIndex:Int){
            this.sheetIndex=sheetIndex
        }
        fun getRotate():Boolean{
            return rotate
        }
        fun getPosition():Vector2f{
            return position
        }

        fun getFormat():String{
            return format
        }

        fun getFilter():Array<String>{
            return filter
        }
        fun getSize():Vector2f{
            return size
        }
        fun getIndex():Int{
            return index
        }
        fun getName():String{
            return name
        }

        fun getSheetIndex():Int{
            return sheetIndex
        }

    }
}