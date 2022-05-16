package com.theoutcasts.app.domain.model

data class Comment(
    var id: String? = null,
    var userId: String? = null,
    var eventId: String? = null,
    var content: String? = null
)
