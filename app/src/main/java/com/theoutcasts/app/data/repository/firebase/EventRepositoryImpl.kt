package com.theoutcasts.app.data.repository.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.repository.EventRepository
import kotlinx.coroutines.tasks.await

class EventRepositoryImpl(
    private val rootReference : DatabaseReference = Firebase.database(DATABASE_CONNECTION_STRING).reference,
    private val nodeReference : DatabaseReference = rootReference.child(NODE_NAME)
) : EventRepository {

    override suspend fun save(event: Event): Result<Event> {
        return try {
            if (event.id == null) {
                val newId = nodeReference.push().key
                event.id = newId
            }
            nodeReference.child(event.id!!).setValue(event).await()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun delete(event: Event): Result<Event> {
        return try {
            nodeReference.child(event.id!!).removeValue().await()
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getById(id: String): Result<Event> {
        return try {
            val event = nodeReference.child(id).get().await().getValue(Event::class.java)
            Result.success(event!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAll(limit: Int): Result<List<Event>> {
        val query = nodeReference.limitToFirst(limit)
        val events = mutableListOf<Event>()
        return try {
            val response = query.get().await()
            for (event in response.children) {
                events.add(event.getValue(Event::class.java)!!)
            }
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getByCreatorId(creatorId: String): Result<List<Event>> {
        val query = nodeReference.orderByChild("creatorId").equalTo(creatorId)
        val events = mutableListOf<Event>()
        return try {
            val response = query.get().await()
            for (event in response.children) {
                events.add(event.getValue(Event::class.java)!!)
            }
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val TAG = "Firebase.EventRepositoryImpl"
        const val NODE_NAME = "events"
        const val DATABASE_CONNECTION_STRING = "https://vk-android-efc7c-default-rtdb.europe-west1.firebasedatabase.app" // TODO: move to config
    }
}
