package com.theoutcasts.app.ui.map

import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class EventMarker(
    map: MapView,
    val eventId: String
) : Marker(map) { }
