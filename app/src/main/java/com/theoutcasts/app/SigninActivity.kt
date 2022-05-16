package com.theoutcasts.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.theoutcasts.app.domain.repository.UserRepository
import com.theoutcasts.app.repository.firebase.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignInActivity : AppCompatActivity() {
    private var userRepository: UserRepository = UserRepositoryImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        findViewById<Button>(R.id.signin_btn_login).setOnClickListener() {
            var email = findViewById<EditText>(R.id.signin_et_email).text.toString().trim()
            var password = findViewById<EditText>(R.id.signin_et_password).text.toString().trim()

            when {
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(
                        this@SignInActivity,
                        "Пожалуйста, введите email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(password) -> {
                    Toast.makeText(
                        this@SignInActivity,
                        "Пожалуйста, введите пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        val authenticatedUser = userRepository.signIn(email, password)
                        if (authenticatedUser == null) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@SignInActivity,
                                    "Не удалось авторизоваться",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }

        findViewById<Button>(R.id.signup_btn_already_registered).setOnClickListener() {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}