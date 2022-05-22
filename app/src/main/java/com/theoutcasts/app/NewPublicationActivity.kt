package com.theoutcasts.app

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timer
import com.theoutcasts.app.data.repository.firebase.EventRepositoryImpl as EventRepositoryImpl1

class NewPublicationActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    private val image: ImageView by lazy { findViewById(R.id.photoFromCamera) }
    private val publicationDescription: TextView by lazy { findViewById(R.id.description_text) }
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageBitmap = it.data?.extras?.get("data") as Bitmap
            image.setImageBitmap(imageBitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_publication)
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            getResult.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }
    fun downloadPhoto(view: View) {
        dispatchTakePictureIntent()
    }

    fun createPublication(view: View) {
        val publicationName = findViewById<TextView?>(R.id.post_name).text.toString()
        val publicationDescription = findViewById<TextView?>(R.id.description_text).text.toString()
        val img = image.drawToBitmap()
        Toast.makeText(this, "publication created " + publicationName + " " + publicationDescription, Toast.LENGTH_LONG).show()
        // загрузка в БД, возврат к предыдущему экрану
        GlobalScope.launch(Dispatchers.IO) {
            val eventsManager = EventInteractor(eventRepository = EventRepositoryImpl1())
            val newEvent = Event()
            newEvent.description = publicationDescription
            newEvent.timeCreated = Date().toString()
            newEvent.latitude = 1.12
            newEvent.longitude = 1.13
            newEvent.likeCount = 0
            newEvent.userId = ""
        }
    }

}