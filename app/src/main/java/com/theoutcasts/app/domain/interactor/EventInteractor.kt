package com.theoutcasts.app.domain.interactor

import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.repository.EventRepository

class EventInteractor(private val eventRepository: EventRepository) {
    suspend fun save(event: Event): Result<Event> = eventRepository.save(event)
    suspend fun delete(event: Event): Result<Event> = eventRepository.delete(event)
    suspend fun getById(id: String): Result<Event> = eventRepository.getById(id)
    suspend fun getAll(limit: Int=15): Result<List<Event>> = eventRepository.getAll(limit)

    suspend fun getByCreatorId(creatorId: String): Result<List<Event>> =
        eventRepository.getByCreatorId(creatorId)
}
