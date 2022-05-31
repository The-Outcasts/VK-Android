package com.theoutcasts.app.ui.map.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.ImageInteractor
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.domain.model.User
import com.theoutcasts.app.ui.map.model.EventUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem

class MapViewModel(
    private val eventInteractor: EventInteractor,
    private val imageInteractor: ImageInteractor,
    private val userInteractor: UserInteractor
) : ViewModel() {

    private val errorMessageLiveData = MutableLiveData<String>()
    private val eventsLiveData = MutableLiveData<List<EventUi>>()

    val errorMessage: LiveData<String> = errorMessageLiveData
    val events: LiveData<List<EventUi>> = eventsLiveData

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun loadEvents() {
        GlobalScope.launch(Dispatchers.IO) {
            // load all events without pictures
            eventInteractor.getAll(100).fold(
                onSuccess = { eventDomainList ->
                    val eventUiList: MutableList<EventUi> = mutableListOf()
                    for (eventDomain in eventDomainList) {
                        eventUiList.add(EventUi(eventDomain, null))
                    }

                    withContext(Dispatchers.Main) {
                        eventsLiveData.value = eventUiList
                    }

                    // load pictures for events
                    for (eventUi in eventUiList) {
                        imageInteractor.downloadImage(eventUi.domain.pictureURL!!).fold(
                            onSuccess = {
                                withContext(Dispatchers.Main) {
                                    eventUi.pictureBitmap = it
                                }
                            },
                            onFailure = {
                                withContext(Dispatchers.Main) {
                                    errorMessageLiveData.value = "Ошибка: ${it.toString()}"
                                }
                            }
                        )
                    }

                    withContext(Dispatchers.Main) {
                        eventsLiveData.value = eventUiList
                    }
                },
                onFailure = { error ->
                    withContext(Dispatchers.Main) {
                        errorMessageLiveData.value = "Ошибка: ${error.toString()}"
                    }
                })
        }
    }
}
