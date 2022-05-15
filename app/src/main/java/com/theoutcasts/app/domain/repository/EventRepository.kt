package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.Event

interface EventRepository {
    suspend fun save(event: Event)
    suspend fun delete(id: String)
    suspend fun getById(id: String): Event?
    suspend fun getAll(limit: Int=15): List<Event>
    suspend fun getByCreatorId(creatorId: String): List<Event>
}
