package com.theoutcasts.app.data.repository.firebase

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theoutcasts.app.domain.model.Comment
import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.repository.CommentRepository
import kotlinx.coroutines.tasks.await

class CommentRepositoryImpl(
    private val nodeReference : DatabaseReference = Firebase.database(DATABASE_CONNECTION_STRING).reference.child(NODE_NAME)
) : CommentRepository{

    override suspend fun save(comment: Comment): Result<Comment> {
        return try {
            if (comment.id == null) {
                val newId = nodeReference.push().key
                comment.id = newId
            }
            nodeReference.child(comment.id!!).setValue(comment).await()
            Result.success(comment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(comment: Comment): Result<Comment> {
        return try {
            nodeReference.child(comment.id!!).removeValue().await()
            Result.success(comment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getById(id: String): Result<Comment> {
        return try {
            val comment = nodeReference.child(id).get().await().getValue(Comment::class.java)
            Result.success(comment!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getByEventId(eventId: String): Result<List<Comment>> {
        val query = nodeReference.orderByChild("eventId").equalTo(eventId)
        val comments = mutableListOf<Comment>()
        return try {
            val response = query.get().await()
            for (comment in response.children) {
                comments.add(comment.getValue(Comment::class.java)!!)
            }
            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val TAG = "Firebase.CommentRepositoryImpl"
        const val NODE_NAME = "comments"
        const val DATABASE_CONNECTION_STRING = "https://vk-android-efc7c-default-rtdb.europe-west1.firebasedatabase.app" // TODO: move to config
    }
}