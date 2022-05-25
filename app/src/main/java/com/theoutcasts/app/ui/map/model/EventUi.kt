package com.theoutcasts.app.ui.map.model

import android.graphics.Bitmap
import com.theoutcasts.app.domain.model.Event

data class EventUi(
    val domain: Event,
    var pictureBitmap: Bitmap? = null
)
