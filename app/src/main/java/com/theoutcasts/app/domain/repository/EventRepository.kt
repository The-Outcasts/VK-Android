package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.model.EventConversation

interface EventRepository {
    /* Event */
    suspend fun save(event: Event): Result<Event>
    suspend fun delete(event: Event): Result<Event>
    suspend fun getById(id: String): Result<Event>
    suspend fun getAll(limit: Int=15): Result<List<Event>>
    suspend fun getByCreatorId(creatorId: String): Result<List<Event>>

    /* EventConversation (likes, comments) */
    suspend fun getEventConversation(eventId: String): Result<EventConversation>
    suspend fun changeLikeCount(eventId: String, delta: Int)
    suspend fun changeCommentCount(eventId: String, delta: Int)
    suspend fun checkIfUserLikedEvent(userId: String, eventId: String): Result<Boolean>
    suspend fun likeEvent(userId: String, eventId: String)
}
