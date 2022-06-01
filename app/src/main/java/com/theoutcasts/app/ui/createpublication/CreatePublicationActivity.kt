package com.theoutcasts.app.ui.createpublication

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.R
import com.theoutcasts.app.ui.map.MainActivity

class CreatePublicationActivity : AppCompatActivity() {
    private lateinit var mViewModel: CreatePublicationViewModel

    private val mImageButtonEventImage: ImageButton by lazy { findViewById(R.id.ibtn_event_image) }
    private val mEditTextEventTitle: EditText by lazy { findViewById(R.id.et_event_title) }
    private val mEditTextEventDescription: EditText by lazy { findViewById(R.id.et_event_description) }
    private val mButtonPublicate: Button by lazy { findViewById(R.id.btn_publicate) }

    private val mLongitude: Double by lazy { intent.getDoubleExtra("longitude", 0.0) }
    private val mLatitude: Double by lazy { intent.getDoubleExtra("latitude", 0.0) }

    private val mTakePictureIntentResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it.data?.extras?.get("data") as Bitmap
                mImageButtonEventImage.setImageBitmap(imageBitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_publication)

        mViewModel = ViewModelProvider(
            this, CreatePublicationViewModelFactory())[CreatePublicationViewModel::class.java]

        setupButtonCallbacks()
        setupLiveDataObservers()
    }

    private fun setupButtonCallbacks() {
        mButtonPublicate.setOnClickListener { publishEvent() }
        mImageButtonEventImage.setOnClickListener { uploadEventImage() }
    }

    private fun setupLiveDataObservers() {
        mViewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }

        mViewModel.eventPictureBitmap.observe(this) { bitmap ->
            mImageButtonEventImage.setImageBitmap(bitmap)
        }

        mViewModel.eventTitle.observe(this) { title ->
            mEditTextEventTitle.setText(title)
        }

        mViewModel.eventDescription.observe(this) { description ->
            mEditTextEventDescription.setText(description)
        }

        mViewModel.eventPublishedFlag.observe(this) { isPublished ->
            if (isPublished) {
                mViewModel.eventPublishedFlag.value = false
                finish()
            }
        }
    }

    private fun uploadEventImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            mTakePictureIntentResult.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Could not launch camera", Toast.LENGTH_LONG).show()
        }
    }

    private fun publishEvent() {
        val eventTitle = mEditTextEventTitle.text.toString().trim()
        val eventDescription = mEditTextEventDescription.text.toString().trim()
        val eventPicture = mImageButtonEventImage.drawToBitmap()

        mViewModel.publishEvent(eventTitle, eventDescription, eventPicture, mLongitude, mLatitude)
    }

    companion object {
        val REQUEST_IMAGE_CAPTURE = 1
    }
}
