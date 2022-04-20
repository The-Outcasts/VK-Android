package com.theoutcasts.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignupActivity : AppCompatActivity() {
    private lateinit var firebaseAuthService: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuthService = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.signup_btn).setOnClickListener() {
            var email = findViewById<EditText>(R.id.signup_et_email).text.toString().trim()
            var password = findViewById<EditText>(R.id.signup_et_password).text.toString().trim()

            when {
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(
                        this@SignupActivity,
                        "Пожалуйста, введите email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(password) -> {
                    Toast.makeText(
                        this@SignupActivity,
                        "Пожалуйста, введите пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    firebaseAuthService.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {
                        if (it.isSuccessful) {
                            val firebaseUser: FirebaseUser = it.result!!.user!!
                            Toast.makeText(
                                this@SignupActivity,
                                "Вы зарегистрированы",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@SignupActivity, SigninActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignupActivity,
                                "Произошла ошибка регистрации",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        findViewById<Button>(R.id.signup_btn_already_registered).setOnClickListener() {
            val intent = Intent(this@SignupActivity, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}