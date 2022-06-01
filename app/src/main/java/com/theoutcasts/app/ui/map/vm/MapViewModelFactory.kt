package com.theoutcasts.app.ui.map.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.data.repository.firebase.EventRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.ImageRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.ImageInteractor
import com.theoutcasts.app.domain.interactor.UserInteractor

class MapViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(
            EventInteractor(EventRepositoryImpl()),
            ImageInteractor(ImageRepositoryImpl())
        ) as T
    }
}
