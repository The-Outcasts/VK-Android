package com.theoutcasts.app.data.repository.firebase

import android.text.BoringLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theoutcasts.app.domain.interactor.EventConversationNotFoundException
import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.model.EventConversation
import com.theoutcasts.app.domain.repository.EventRepository
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class EventRepositoryImpl(
    private val rootReference : DatabaseReference = Firebase.database(DATABASE_CONNECTION_STRING).reference,
    private val nodeReference : DatabaseReference = rootReference.child(NODE_NAME)
) : EventRepository {

    /* Event */
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


    /* EventConversation */
    override suspend fun getEventConversation(eventId: String): Result<EventConversation> {
        val query = rootReference
            .child(EVENT_CONVERSATION_NODE_NAME)
            .child(eventId)
        try {
            val response = query.get().await().getValue(EventConversation::class.java)
            return Result.success(response!!)
        } catch (e: NullPointerException) {
            val eventResponse = getById(eventId)    /* check if event with this id is exists */
            if (eventResponse.isFailure) {
                eventResponse.onFailure { e ->
                    return Result.failure(e)
                }
            }

            return Result.success(EventConversation())  /* return new conversation with likes=0, comments=0 */
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun changeCommentCount(eventId: String, delta: Int) {
        rootReference
            .child(EVENT_CONVERSATION_NODE_NAME)
            .child(eventId)
            .child("commentCount")
            .runTransaction(object : Transaction.Handler {

                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var value: Int? = currentData.getValue(Int::class.java)
                    if (value == null) {
                        currentData.value = 1
                    } else {
                        var result = value + delta
                        if (result < 0) {
                            result = 0
                        }
                        currentData.value = result
                    }

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    error?.let {
                        Timber.e("EventRepository.increaseCommentCount error: $error")
                    }
                }
            })
    }

    override suspend fun changeLikeCount(eventId: String, delta: Int) {
        rootReference
            .child(EVENT_CONVERSATION_NODE_NAME)
            .child(eventId)
            .child("likeCount")
            .runTransaction(object : Transaction.Handler {

                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    var value: Int? = currentData.getValue(Int::class.java)
                    if (value == null) {
                        currentData.value = 1
                    } else {
                        var result: Int = value + delta
                        if (result < 0) {
                            result = 0
                        }
                        currentData.value = result
                    }

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    error?.let {
                        Timber.e("EventRepository.increaseLikeCount error: $error")
                    }
                }
            })
    }

    override suspend fun checkIfUserLikedEvent(userId: String, eventId: String): Result<Boolean> {
        try {
            val currentLikeState = rootReference
                .child(LIKES_NODE_NAME)
                .child(eventId)
                .child(userId)
                .get().await()

            if (currentLikeState.exists()) {
                return Result.success(true)
            }
            return Result.success(false)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun likeEvent(userId: String, eventId: String) {
        try {
            val currentLikeState = rootReference
                .child(LIKES_NODE_NAME)
                .child(eventId)
                .child(userId)
                .get().await()

            if (currentLikeState.exists()) {
                rootReference
                    .child(LIKES_NODE_NAME)
                    .child(eventId)
                    .child(userId).removeValue()
            } else {
                rootReference
                    .child(LIKES_NODE_NAME)
                    .child(eventId)
                    .child(userId).setValue(true)
            }

        } catch (e: Exception) {
            Timber.e("[Firebase.EventRepositoryImpl].likeEvent() failed: $e")
        }
    }

    companion object {
        private const val EVENT_CONVERSATION_NODE_NAME = "event_conversation"
        private const val LIKES_NODE_NAME = "likes"

        const val TAG = "Firebase.EventRepositoryImpl"
        const val NODE_NAME = "events"
        const val DATABASE_CONNECTION_STRING = "https://vk-android-efc7c-default-rtdb.europe-west1.firebasedatabase.app" // TODO: move to config
    }
}
