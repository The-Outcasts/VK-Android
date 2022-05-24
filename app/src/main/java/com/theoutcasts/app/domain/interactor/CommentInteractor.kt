package com.theoutcasts.app.domain.interactor

import com.theoutcasts.app.domain.model.Comment
import com.theoutcasts.app.domain.repository.CommentRepository

class CommentInteractor(private val commentRepository: CommentRepository) {
    suspend fun save(comment: Comment): Result<Comment> = commentRepository.save(comment)
    suspend fun delete(comment: Comment): Result<Comment> = commentRepository.delete(comment)
    suspend fun getById(id: String): Result<Comment> = commentRepository.getById(id)

    suspend fun getByEventId(eventId: String): Result<List<Comment>> {
        val commentsResult = commentRepository.getByEventId(eventId)

        commentsResult.fold(
            onSuccess = { comments ->
                val sortedComments = comments.sortedBy { it.timestamp }
                return Result.success(sortedComments)
            },
            onFailure = {
                return commentsResult
            }
        )
    }
}
