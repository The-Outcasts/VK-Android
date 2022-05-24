package com.theoutcasts.app.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Comment(
    var id: String? = null,
    var userId: String? = null,
    var username: String? = null,
    var eventId: String? = null,
    var content: String? = null,
    var timestamp: String? = null,
) {
    init {
        if (timestamp == null) {
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.CANADA)
            val currentDatetime = dateFormatter.format(Date())
            timestamp = currentDatetime.toString()
        }
    }
}
