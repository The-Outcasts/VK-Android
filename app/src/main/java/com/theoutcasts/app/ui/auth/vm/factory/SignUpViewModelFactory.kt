package com.theoutcasts.app.ui.auth.vm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.ui.auth.vm.SignUpViewModel

class SignUpViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpViewModel(UserInteractor(UserRepositoryImpl())) as T
    }
}
