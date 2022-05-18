package com.theoutcasts.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.theoutcasts.app.domain.interactor.InvalidCredentialsException
import com.theoutcasts.app.domain.interactor.UserAlreadyExistsException
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.domain.interactor.WeakPasswordException
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private var userInteractor = UserInteractor(UserRepositoryImpl())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<Button>(R.id.signup_btn).setOnClickListener() {
            val email = findViewById<EditText>(R.id.signup_et_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.signup_et_password).text.toString().trim()

            when {
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Пожалуйста, введите email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(password) -> {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Пожалуйста, введите пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    GlobalScope.launch {
                        val authResult = userInteractor.signUpWithEmailAndPassword(email, password)
                        authResult.fold(
                            onSuccess = {
                                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            onFailure = { e ->
                                var errorMessage = ""
                                when (e) {
                                    is UserAlreadyExistsException ->
                                        errorMessage = "Пользователь с таким email уже существует"
                                    is InvalidCredentialsException ->
                                        errorMessage = "Email форматирован неправильно"
                                    is WeakPasswordException ->
                                        errorMessage = "Слишком слабый пароль, требуется больше 6 символов"
                                }
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT)
                                        .show()
                                }
                        })
                    }
                }
            }
        }

        findViewById<Button>(R.id.signup_btn_already_registered).setOnClickListener() {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}