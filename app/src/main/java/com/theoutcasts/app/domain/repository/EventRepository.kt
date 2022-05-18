package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.Event

interface EventRepository {
    suspend fun save(event: Event): Result<Event>
    suspend fun delete(event: Event): Result<Event>
    suspend fun getById(id: String): Result<Event>
    suspend fun getAll(limit: Int=15): Result<List<Event>>
    suspend fun getByCreatorId(creatorId: String): Result<List<Event>>
}
