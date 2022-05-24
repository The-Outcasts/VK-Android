package com.theoutcasts.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.theoutcasts.app.data.repository.firebase.ImageRepositoryImpl
import com.theoutcasts.app.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityTest : AppCompatActivity() {
    private lateinit var btnLoad: Button
    private lateinit var btnUpload: Button

    private lateinit var etUrl: EditText
    private lateinit var ivImg: ImageView

    private val imageRepository: ImageRepository = ImageRepositoryImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)

        btnLoad = findViewById(R.id.button_load)
        btnUpload = findViewById(R.id.button_upload)
        etUrl = findViewById(R.id.etUrl)
        ivImg = findViewById(R.id.iv)

        btnLoad.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val imgResult = imageRepository.downloadImage(etUrl.text.toString())
                imgResult.fold(
                    onSuccess = { bitmap ->
                        withContext(Dispatchers.Main) {
                            ivImg.setImageBitmap(bitmap)
                        }
                    },
                    onFailure = { e ->
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivityTest, "Error ${e.toString()}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }

        btnUpload.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivity(Intent.createChooser(intent, "select?"))
        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}