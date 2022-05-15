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

class SigninActivity : AppCompatActivity() {
    private lateinit var firebaseAuthService: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        firebaseAuthService = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.signin_btn_login).setOnClickListener() {
            var email = findViewById<EditText>(R.id.signin_et_email).text.toString().trim()
            var password = findViewById<EditText>(R.id.signin_et_password).text.toString().trim()

            when {
                TextUtils.isEmpty(email) -> {
                    Toast.makeText(
                        this@SigninActivity,
                        "Пожалуйста, введите email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(password) -> {
                    Toast.makeText(
                        this@SigninActivity,
                        "Пожалуйста, введите пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    firebaseAuthService.signInWithEmailAndPassword(email, password).addOnCompleteListener() {
                        if (it.isSuccessful) {
                            val firebaseUser: FirebaseUser = it.result!!.user!!
                            val intent = Intent(this@SigninActivity, MainActivity::class.java)
                            intent.putExtra("user_id", firebaseUser.uid)
                            intent.putExtra("email", firebaseUser.email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@SigninActivity,
                                "Не удалось авторизоваться",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        findViewById<Button>(R.id.signup_btn_already_registered).setOnClickListener() {
            val intent = Intent(this@SigninActivity, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}