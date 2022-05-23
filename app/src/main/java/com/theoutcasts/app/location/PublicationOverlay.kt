package com.theoutcasts.app.location

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.theoutcasts.app.MainActivity
import com.theoutcasts.app.PublicationActivity
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Overlay

public class PublicationOverlay(context: Context) : Overlay() {


    private lateinit var mIcon:Bitmap
    private lateinit var mImage:Bitmap
    private lateinit var position:GeoPoint
    private var iconMatrix = Matrix()
    private var imageMatrix = Matrix()
    private val imageWidth = 150
    private val imageHeight = 100
    private val iconWidth = 160
    private val iconHeight = 160
    private val mainContext = context


    //private lateinit var iBackground: Bitmap
    //private val mPaint: Paint = ""



    fun setImage(newImage: Bitmap) {
        mImage = Bitmap.createScaledBitmap(newImage,imageWidth,imageHeight,false)
    }

    fun setIcon(newIcon: Bitmap) {
        mIcon = Bitmap.createScaledBitmap(newIcon,iconWidth,iconHeight,false)
    }
    fun getImage(): Bitmap {
        return mImage
    }

    fun setPosition(oPosition: GeoPoint) {
        position = oPosition
    }

    fun getPosition(): GeoPoint {
        return position
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
        imageMatrix.postTranslate((x0+((iconWidth-imageWidth)/2).toLong()).toFloat(),(y0+((iconHeight-imageHeight)/6).toFloat()))

    }

    override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
        val intent = Intent(mainContext, PublicationActivity::class.java)
        val bundle = Bundle()
        intent.putExtra("eventId", "-N27ldaObFiwsu-WEiII")
        startActivity(mainContext, intent, bundle)
        return super.onSingleTapConfirmed(e, mapView)
    }
}