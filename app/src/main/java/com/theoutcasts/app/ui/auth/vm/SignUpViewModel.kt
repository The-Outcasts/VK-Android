package com.theoutcasts.app.ui.auth.vm

import android.text.BoringLayout
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theoutcasts.app.domain.interactor.InvalidCredentialsException
import com.theoutcasts.app.domain.interactor.UserAlreadyExistsException
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.domain.interactor.WeakPasswordException
import com.theoutcasts.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel(
    private val userInteractor: UserInteractor
) : ViewModel() {

    private val errorMessageLiveData = MutableLiveData<String>()
    val errorMessage: LiveData<String> = errorMessageLiveData

    private val signedUserLiveData = MutableLiveData<User>()
    val signedUser: LiveData<User> = signedUserLiveData

    fun signUpWithEmailPasswordAndUsername(email: String, password: String, username: String) {
        when {
            TextUtils.isEmpty(email) -> errorMessageLiveData.value = "Пожалуйста, введите email"
            TextUtils.isEmpty(username) -> errorMessageLiveData.value = "Пожалуйста, введите имя пользователя"
            TextUtils.isEmpty(password) -> errorMessageLiveData.value = "Пожалуйста, введите пароль"
            else -> {
                GlobalScope.launch(Dispatchers.IO) {
                    val authResult = userInteractor.signUpWithEmailPasswordAndUsername(email, password, username)
                    authResult.fold(
                        onSuccess = { user ->
                            withContext(Dispatchers.Main) { signedUserLiveData.value = user }
                        },
                        onFailure = { e ->
                            withContext(Dispatchers.Main) {
                                when (e) {
                                    is UserAlreadyExistsException -> errorMessageLiveData.value = "Пользователь с таким email уже существует"
                                    is InvalidCredentialsException -> errorMessageLiveData.value = "Email форматирован неправильно"
                                    is WeakPasswordException -> errorMessageLiveData.value = "Слишком слабый пароль, требуется больше 6 символов"
                                }
                            }
                        })
                }
            }
        }
    }
}
