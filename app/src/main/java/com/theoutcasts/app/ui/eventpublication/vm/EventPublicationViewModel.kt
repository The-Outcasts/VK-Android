package com.theoutcasts.app.ui.eventpublication.vm

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theoutcasts.app.domain.interactor.CommentInteractor
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.ImageInteractor
import com.theoutcasts.app.domain.interactor.UserInteractor
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

    private val mErrorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = mErrorMessage

    private val mEventPicture = MutableLiveData<Bitmap>()
    val eventPicture: LiveData<Bitmap> = mEventPicture

    private val mEventDescription = MutableLiveData<String>()
    val eventDescription: LiveData<String> = mEventDescription

    private val mComments = MutableLiveData<ArrayList<Comment>>()
    val comments: LiveData<ArrayList<Comment>> = mComments

    private val postedCommentFlag = MutableLiveData<Boolean>()

    private var mEvent: Event? = null

    fun loadEvent(eventId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            mEventInteractor.getById(eventId)
                .onSuccess { event ->
                    mEvent = event
                    withContext(Dispatchers.Main) { mEventDescription.value = event.description!! }

                    mImageInteractor.downloadImage(event.pictureURL!!)
                        .onSuccess { bitmap ->
                            withContext(Dispatchers.Main) { mEventPicture.value = bitmap }
                        }.onFailure { e ->
                            withContext(Dispatchers.Main) { mErrorMessage.value = e.toString() }
                        }
                }.onFailure { e ->
                    withContext(Dispatchers.Main) { mErrorMessage.value = e.toString() }
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
            mUserInteractor.getAuthenticatedUser().onSuccess { user ->
                val comment = Comment().apply {
                    userId = user.id
                    username = user.username
                    eventId = mEvent!!.id
                    content = aContent
                    timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.CANADA)
                        .format(Date())
                        .toString()
                }

                mCommentInteractor.save(comment).onFailure { e ->
                    withContext(Dispatchers.Main) { mErrorMessage.value = e.toString()}
                }

                withContext(Dispatchers.Main) { mComments.value!!.add(comment) }
            }
        }
    }

    fun likePublication() {

    }
}
