package com.theoutcasts.app.ui.createpublication

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.data.repository.firebase.EventRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.ImageRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.ImageInteractor
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.domain.model.Event
import com.theoutcasts.app.domain.util.TimeStampFormater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatePublicationViewModel(
    private val mEventInteractor: EventInteractor,
    private val mUserInteractor: UserInteractor,
    private val mImageInteractor: ImageInteractor
) : ViewModel() {

    private val mErrorMessage = MutableLiveData<String>()
    private val mEventPictureBitmap = MutableLiveData<Bitmap>()
    private val mEventTitle = MutableLiveData<String>()
    private val mEventDescription = MutableLiveData<String>()

    val errorMessage: LiveData<String> = mErrorMessage
    val eventPictureBitmap: LiveData<Bitmap> = mEventPictureBitmap
    val eventTitle: LiveData<String> = mEventTitle
    val eventDescription: LiveData<String> = mEventDescription

    val eventPublishedFlag = MutableLiveData<Boolean>(false)

    fun publishEvent(eventTitle: String, eventDescription: String, eventPicture: Bitmap,
                     longitude: Double, latitude: Double) {
        when {
            TextUtils.isEmpty(eventTitle) -> mErrorMessage.value = "Пожалуйста, добавьте заголовок"
            TextUtils.isEmpty(eventDescription) -> mErrorMessage.value = "Пожалуйста, добавьте описание"
            else -> {
                val userId = mUserInteractor.getAuthenticatedUserId()!!

                var event = Event()
                event.description = eventDescription
                event.longitude = longitude
                event.latitude = latitude
                event.userId = userId
                event.timeCreated = TimeStampFormater.getCurrentDateAsString()
                event.likeCount = 0

                /* event.title = eventTitle */

                GlobalScope.launch(Dispatchers.IO) {
                    mImageInteractor.uploadImage(eventPicture, userId)
                        .onSuccess { pictureURL ->
                            event.pictureURL = pictureURL

                            mEventInteractor.save(event)

                            withContext(Dispatchers.Main) {
                                eventPublishedFlag.value = true
                            }
                        }.onFailure { e ->
                            withContext(Dispatchers.Main) {
                                mErrorMessage.value = "Не удалось опубликовать событие: $e"
                            }
                        }
                }
            }
        }
    }
}

class CreatePublicationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreatePublicationViewModel(
            EventInteractor(EventRepositoryImpl()),
            UserInteractor(UserRepositoryImpl()),
            ImageInteractor(ImageRepositoryImpl())
        ) as T
    }
}
