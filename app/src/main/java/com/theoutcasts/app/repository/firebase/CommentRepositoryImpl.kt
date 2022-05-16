package com.theoutcasts.app.repository.firebase

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theoutcasts.app.domain.model.Comment
import com.theoutcasts.app.domain.repository.CommentRepository
import kotlinx.coroutines.tasks.await

class CommentRepositoryImpl(
    private val nodeReference : DatabaseReference =
        Firebase.database(DATABASE_CONNECTION_STRING).reference.child(NODE_NAME)
) : CommentRepository{

    override suspend fun save(comment: Comment) {
        try {
            if (comment.id == null) {
                val generatedId = nodeReference.push().key
                comment.id = generatedId
            }
            nodeReference.child(comment.id!!).setValue(comment).await()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    override suspend fun delete(id: String) {
        try {
            nodeReference.child(id).removeValue().await()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    override suspend fun getById(id: String): Comment? {
        var result: Comment? = null
        try {
            result = nodeReference.child(id).get().await().getValue(Comment::class.java)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return result
    }

    override suspend fun getByEventId(eventId: String): List<Comment> {
        val query = nodeReference.orderByChild("eventId").equalTo(eventId)
        val result = mutableListOf<Comment>()
        try {
            val response = query.get().await()
            for (comment in response.children) {
                result.add(comment.getValue(Comment::class.java)!!)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        return result
    }

    companion object {
        const val TAG = "Firebase.CommentRepositoryImpl"
        const val NODE_NAME = "comments"
        const val DATABASE_CONNECTION_STRING = "https://vk-android-efc7c-default-rtdb.europe-west1.firebasedatabase.app" // TODO: move to config
    }
}