package com.theoutcasts.app.domain.interactor

import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.model.EventConversation
import com.theoutcasts.app.domain.repository.EventRepository

class EventConversationNotFoundException : Exception("There is no conversation data for this event")

class EventInteractor(private val eventRepository: EventRepository) {
    suspend fun save(event: Event): Result<Event> =
        eventRepository.save(event)

    suspend fun delete(event: Event): Result<Event> =
        eventRepository.delete(event)

    suspend fun getById(id: String): Result<Event> =
        eventRepository.getById(id)

    suspend fun getAll(limit: Int=15): Result<List<Event>> =
        eventRepository.getAll(limit)

    suspend fun getByCreatorId(creatorId: String): Result<List<Event>> =
        eventRepository.getByCreatorId(creatorId)

    suspend fun getEventConversation(eventId: String): Result<EventConversation> =
        eventRepository.getEventConversation(eventId)

    suspend fun changeLikeCount(eventId: String, delta: Int) =
        eventRepository.changeLikeCount(eventId, delta)

    suspend fun changeCommentCount(eventId: String, delta: Int) =
        eventRepository.changeCommentCount(eventId, delta)

    suspend fun checkIfUserLikedEvent(userId: String, eventId: String): Result<Boolean> =
        eventRepository.checkIfUserLikedEvent(userId, eventId)
}
