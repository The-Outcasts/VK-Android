package com.theoutcasts.app.data.repository.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.theoutcasts.app.domain.repository.ImageRepository
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageRepositoryImpl: ImageRepository {
    private val storage = Firebase.storage(PATH).reference

    override suspend fun uploadImage(bitmap: Bitmap, userId: String, prefix: String?): Result<String> {
        return try {
            val dateFormatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
            val url = userId + dateFormatter.format(Date())

            val byteStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)

            var node = storage
            if (prefix != null) {
                node = node.child(prefix)
            }

            node.child(url).putBytes(byteStream.toByteArray())

            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadImage(url: String): Result<Bitmap> {
        return try {
            val fileToLoad: File = File.createTempFile("tmp_img", "jpg")
            storage.child(url).getFile(fileToLoad).await()
            val bitmap = BitmapFactory.decodeFile(fileToLoad.absolutePath)
            Result.success(bitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val PATH = "gs://vk-android-efc7c.appspot.com"
    }
}
