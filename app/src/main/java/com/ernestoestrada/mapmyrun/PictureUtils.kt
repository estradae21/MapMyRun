package com.ernesto.neil.fitnessmapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

/**
 * Created by neilk on 5/8/2018.
 */
class PictureUtils
{
    fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int) : Bitmap
    {
        //Read in the dimensions of the image on disk

        var options : BitmapFactory.Options = BitmapFactory.Options()

        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        var srcWidth : Float = options.outWidth.toFloat()
        var srcHeight : Float = options.outHeight.toFloat()

        //Figure out how much to scale down by
        var inSampleSize : Int = 1
        if(srcHeight > destHeight || srcWidth > destWidth)
        {
            var heightScale = srcHeight / destHeight
            var widthScale = srcWidth / destWidth

            if(heightScale > widthScale) {
                inSampleSize = Math.round(heightScale)
            }
            else
            {
                inSampleSize = Math.round(widthScale)
            }
        }

        options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options)

    }

    fun getScaledBitmap(path: String, activity: Activity) : Bitmap
    {
        var size : Point = Point()
        activity.windowManager.defaultDisplay.getSize(size)

        return getScaledBitmap(path, size.x, size.y)
    }
}