package com.theoutcasts.app.domain.repository

import com.theoutcasts.app.domain.model.Comment

interface CommentRepository {
    suspend fun save(comment: Comment): Result<Comment>
    suspend fun delete(comment: Comment): Result<Comment>
    suspend fun getById(id: String): Result<Comment>
    suspend fun getByEventId(eventId: String): Result<List<Comment>>
}
