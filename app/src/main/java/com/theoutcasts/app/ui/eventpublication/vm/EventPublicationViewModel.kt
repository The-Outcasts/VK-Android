package com.theoutcasts.app.ui.eventpublication.vm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theoutcasts.app.domain.interactor.*
import com.theoutcasts.app.domain.model.Comment
import com.theoutcasts.app.domain.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventPublicationViewModel(
    private val mEventInteractor: EventInteractor,
    private val mUserInteractor: UserInteractor,
    private val mCommentInteractor: CommentInteractor,
    private val mImageInteractor: ImageInteractor
) : ViewModel() {

    private var mEvent: Event? = null

    private val mErrorMessage = MutableLiveData<String>()
    private val mEventPicture = MutableLiveData<Bitmap>()
    private val mEventDescription = MutableLiveData<String>()
    private val mComments = MutableLiveData<ArrayList<Comment>>()
    private val mCommentCount = MutableLiveData(0)
    private val mLikeCount = MutableLiveData(0)
    private var mIsCurrentUserLikedEvent = MutableLiveData(false)

    val errorMessage: LiveData<String> = mErrorMessage
    val eventPicture: LiveData<Bitmap> = mEventPicture
    val eventDescription: LiveData<String> = mEventDescription
    val comments: LiveData<ArrayList<Comment>> = mComments
    val commentCount: LiveData<Int> = mCommentCount
    val likeCount: LiveData<Int> = mLikeCount
    val isCurrentUserLikedEvent: LiveData<Boolean> = mIsCurrentUserLikedEvent

    val postedCommentFlag = MutableLiveData<Boolean>()

    fun loadEvent(eventId: String) {
        GlobalScope.launch(Dispatchers.IO) {

            /* Load event data */
            mEventInteractor.getById(eventId)
                .onSuccess { event ->
                    mEvent = event
                    withContext(Dispatchers.Main) {
                        mEventDescription.value = event.description!!
                    }

                    /* Load event conversation */
                    mEventInteractor.getEventConversation(eventId)
                        .onSuccess { conversation ->
                            withContext(Dispatchers.Main) {
                                mCommentCount.value = conversation.commentCount
                                mLikeCount.value = conversation.likeCount
                            }
                        }.onFailure { e ->
                            withContext(Dispatchers.Main) {
                                mErrorMessage.value = "Не удалось загрузить данные об обсуждении события: $e"
                            }
                        }

                    /* Check if user liked this event */
                    /*
                    mEventInteractor.checkIfUserLikedEvent(CurrentUser.value!!.id, eventId)
                        .onSuccess { isLiked ->
                            mIsCurrentUserLikedEvent.value = isLiked
                        }.onFailure { e ->
                            withContext(Dispatchers.Main) {
                                mErrorMessage.value = "Произошла ошибка: $e"
                            }
                        }
                    */

                    /* Load event picture */
                    mImageInteractor.downloadImage(event.pictureURL!!)
                        .onSuccess { bitmap ->
                            withContext(Dispatchers.Main) {
                                mEventPicture.value = bitmap
                            }

                        }.onFailure { e ->
                            withContext(Dispatchers.Main) {
                                mErrorMessage.value = "Не удалось загрузить изображение: $e"
                            }
                        }
                }.onFailure { e ->
                    withContext(Dispatchers.Main) {
                        mErrorMessage.value = "Не удалось загрузить событие: $e"
                    }
                }
        }
    }

    fun loadComments() {
        if (mEvent == null) {
            mErrorMessage.value = "Ошибка определения идентификатора публикации"
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            mCommentInteractor.getByEventId(mEvent!!.id!!)
                .onSuccess { comments ->
                    withContext(Dispatchers.Main) { mComments.value = ArrayList(comments) }
                }.onFailure { e ->
                    withContext(Dispatchers.Main) { mErrorMessage.value = e.toString() }
                }
        }
    }

    fun postComment(aContent: String) {
        GlobalScope.launch(Dispatchers.IO) {

            /* Additional check if user is authenticated */
            mUserInteractor.getAuthenticatedUser()
                .onSuccess { user ->
                    val comment = Comment().apply {
                        userId = user.id
                        username = user.username
                        eventId = mEvent!!.id!!
                        content = aContent
                        timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.CANADA)
                            .format(Date())
                            .toString()
                    }

                    withContext(Dispatchers.Main) {
                        /* Maybe it should be done another way, but otherwise LiveData observer doesn't triggers */
                        val updatedCommentCount = mCommentCount.value!! + 1
                        mCommentCount.value = updatedCommentCount

                        mComments.value!!.add(comment)
                        postedCommentFlag.value = true
                    }

                    mCommentInteractor.save(comment)
                        .onFailure { e ->
                            withContext(Dispatchers.Main) {
                                mErrorMessage.value = e.toString()
                            }
                        }

                    mEventInteractor.changeCommentCount(mEvent!!.id!!, 1)

                }.onFailure { e ->
                    withContext(Dispatchers.Main) {
                        mErrorMessage.value = "Ошибка авторизации: $e"
                    }
                }
        }
    }

    fun likePublication() {
        val deltaLikeCount = if (mIsCurrentUserLikedEvent.value!!) -1 else 1

        GlobalScope.launch(Dispatchers.IO) {
            mEventInteractor.changeLikeCount(mEvent!!.id!!, deltaLikeCount)

            withContext(Dispatchers.Main) {
                val updatedLikeCount = mLikeCount.value!! + deltaLikeCount
                mLikeCount.value = updatedLikeCount

                val updatedState = !(mIsCurrentUserLikedEvent.value!!)
                mIsCurrentUserLikedEvent.value = updatedState
            }
        }
    }
}
