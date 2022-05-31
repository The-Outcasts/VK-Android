package com.theoutcasts.app.location

import android.graphics.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Overlay

class PublicationOverlay : Overlay() {
    private lateinit var mIcon:Bitmap
    private lateinit var mImage:Bitmap
    private lateinit var position:GeoPoint
    private lateinit var eventID:String

    private var iconMatrix = Matrix()
    private var imageMatrix = Matrix()
    private val imageWidth = 150
    private val imageHeight = 100
    private val iconWidth = 160
    private val iconHeight = 160


    fun setImage(newImage: Bitmap) {
        mImage = Bitmap.createScaledBitmap(newImage,imageWidth,imageHeight,false)
    }

    fun setIcon(newIcon: Bitmap) {
        mIcon = Bitmap.createScaledBitmap(newIcon,iconWidth,iconHeight,false)
    }

    fun setPosition(oPosition: GeoPoint) {
        position = oPosition
    }

    fun setEventId(clickedEventId: String)   {
        eventID = clickedEventId
    }


    override fun draw(pCanvas: Canvas, pProjection: Projection)  {
        computeMatrix(pProjection)
        pCanvas.drawBitmap(mIcon,iconMatrix, Paint(Paint.ANTI_ALIAS_FLAG))
        pCanvas.drawBitmap(mImage,imageMatrix, Paint(Paint.ANTI_ALIAS_FLAG))
    }

    private fun computeMatrix(pProjection: Projection) {
        iconMatrix.reset()
        imageMatrix.reset()
        val x0 = pProjection.getLongPixelXFromLongitude(position.longitude)-imageWidth.toLong()/2
        val y0 = pProjection.getLongPixelYFromLatitude(position.latitude)-imageHeight.toLong()

        iconMatrix.postTranslate(x0.toFloat(),y0.toFloat())
        imageMatrix.postTranslate(
            (x0+((iconWidth-imageWidth)/2).toLong()).toFloat(),
            (y0+((iconHeight-imageHeight)/6).toFloat())
        )

    }
}
