package com.theoutcasts.app.domain.repository

import android.graphics.Bitmap

interface ImageRepository {
    suspend fun uploadImage(bitmap: Bitmap, userId: String, prefix: String? = null): Result<String>
    suspend fun downloadImage(url: String): Result<Bitmap>
}
