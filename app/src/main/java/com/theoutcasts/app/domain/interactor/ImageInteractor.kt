package com.theoutcasts.app.domain.interactor

import android.graphics.Bitmap
import com.theoutcasts.app.domain.repository.ImageRepository

class ImageInteractor(private val ImageRepository: ImageRepository) {
    suspend fun uploadImage(bitmap: Bitmap, userId: String, prefix: String? = null): Result<String> = ImageRepository.uploadImage(bitmap, userId)
    suspend fun downloadImage(url: String): Result<Bitmap> = ImageRepository.downloadImage(url)
}