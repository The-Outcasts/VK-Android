package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.Comment


interface CommentRepository {
    suspend fun save(comment: Comment)
    suspend fun delete(id: String)
    suspend fun getById(id: String): Comment?
    suspend fun getByEventId(eventId: String): List<Comment>
}
