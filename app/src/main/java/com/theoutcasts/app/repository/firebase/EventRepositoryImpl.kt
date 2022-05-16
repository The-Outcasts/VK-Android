package com.theoutcasts.app.repository.firebase

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.repository.EventRepository
import kotlinx.coroutines.tasks.await
import kotlin.Exception

class EventRepositoryImpl(
    private val rootReference : DatabaseReference =
        Firebase.database(DATABASE_CONNECTION_STRING).reference,
    private val nodeReference : DatabaseReference = rootReference.child(NODE_NAME)
) : EventRepository {

    override suspend fun save(event: Event) {
        try {
            if (event.Id == null) {
                val generatedId = nodeReference.push().key
                event.Id = generatedId
            }
            nodeReference.child(event.Id!!).setValue(event).await()
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

    override suspend fun getById(id: String): Event? {
        var result: Event? = null
        try {
            result = nodeReference.child(id).get().await().getValue(Event::class.java)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return result
    }

    override suspend fun getAll(limit: Int): List<Event> {
        val query = nodeReference.limitToFirst(limit)
        val result = mutableListOf<Event>()
        try {
            val response = query.get().await()
            for (event in response.children) {
                result.add(event.getValue(Event::class.java)!!)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        return result
    }

    override suspend fun getByCreatorId(creatorId: String): List<Event> {
        val query = nodeReference.orderByChild("creatorId").equalTo(creatorId)
        val result = mutableListOf<Event>()
        try {
            val response = query.get().await()
            for (event in response.children) {
                result.add(event.getValue(Event::class.java)!!)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        return result
    }

    companion object {
        const val TAG = "Firebase.EventRepositoryImpl"
        const val NODE_NAME = "events"
        const val DATABASE_CONNECTION_STRING = "https://vk-android-efc7c-default-rtdb.europe-west1.firebasedatabase.app" // TODO: move to config
    }
}
