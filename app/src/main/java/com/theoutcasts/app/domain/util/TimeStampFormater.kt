package com.theoutcasts.app.domain.util

import java.text.SimpleDateFormat
import java.util.*

object TimeStampFormater {
    fun getCurrentDateAsString() : String {
        val timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.CANADA)
            .format(Date())
            .toString()
        return timestamp
    }
}
