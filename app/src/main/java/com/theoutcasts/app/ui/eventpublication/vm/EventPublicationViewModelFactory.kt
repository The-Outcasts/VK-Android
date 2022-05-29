package com.theoutcasts.app.ui.eventpublication.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.data.repository.firebase.CommentRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.EventRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.ImageRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import com.theoutcasts.app.domain.interactor.CommentInteractor
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.ImageInteractor
import com.theoutcasts.app.domain.interactor.UserInteractor

class EventPublicationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EventPublicationViewModel(
            EventInteractor(EventRepositoryImpl()),
            UserInteractor(UserRepositoryImpl()),
            CommentInteractor(CommentRepositoryImpl()),
            ImageInteractor(ImageRepositoryImpl())
        ) as T
    }
}