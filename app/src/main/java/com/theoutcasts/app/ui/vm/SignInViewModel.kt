package com.theoutcasts.app.ui.vm

import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theoutcasts.app.MainActivity
import com.theoutcasts.app.domain.interactor.InvalidLoginOrPasswordException
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(
    private val userInteractor: UserInteractor
) : ViewModel() {

    private val errorMessageLiveData = MutableLiveData<String>()
    val errorMessage: LiveData<String> = errorMessageLiveData

    private val signedUserLiveData = MutableLiveData<User>()
    val signedUser: LiveData<User> = signedUserLiveData

    fun signInWithEmailAndPassword(email: String, password: String) {
        when {
            TextUtils.isEmpty(email) -> errorMessageLiveData.value = "Пожалуйста, введите email"
            TextUtils.isEmpty(password) -> errorMessageLiveData.value = "Пожалуйста, введите пароль"
            else -> {
                GlobalScope.launch(Dispatchers.IO) {
                    val authResult = userInteractor.signInWithEmailAndPassword(email, password)
                    authResult.fold(
                        onSuccess = { user ->
                            withContext(Dispatchers.Main) { signedUserLiveData.value = user }
                        },
                        onFailure = { e ->
                            withContext(Dispatchers.Main) {
                                if (e is InvalidLoginOrPasswordException) {
                                    errorMessageLiveData.value = "Не удалось авторизоваться"
                                } else {
                                    errorMessageLiveData.value = "Ошибка: ${e.toString()}"
                                }
                            }
                        })
                }
            }
        }
    }
}