package com.theoutcasts.app.domain.model

data class Event(
    var Id: String? = null,
    var userId: String? = null,
    var timeCreated: String? = null,
    var pictureURL: String? = null,
    var description: String? = null,
    var longitude: Double? = null,
    var latitude: Double? = null,
    var likeCount: Int? = null
)
