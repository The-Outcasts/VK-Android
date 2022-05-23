package com.theoutcasts.app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.MainActivity
import com.theoutcasts.app.R
import com.theoutcasts.app.ui.vm.SignUpViewModel
import com.theoutcasts.app.ui.vm.factory.SignUpViewModelFactory


class SignUpActivity : AppCompatActivity() {
    private lateinit var vm: SignUpViewModel

    private val emailEditText: EditText by lazy { findViewById(R.id.signup_et_email) }
    private val usernameEditText: EditText by lazy { findViewById(R.id.signup_et_username) }
    private val passwordEditText: EditText by lazy { findViewById(R.id.signup_et_password) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        vm = ViewModelProvider(this, SignUpViewModelFactory())[SignUpViewModel::class.java]

        vm.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT)
                .show()
        })

        vm.signedUser.observe(this, Observer { user ->
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            intent.putExtra("current_user_id", user.id)
            intent.putExtra("current_user_email", user.email)
            intent.putExtra("current_user_username", user.username)
            startActivity(intent)
        })

        findViewById<Button>(R.id.signup_btn).setOnClickListener() {
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            vm.signUpWithEmailPasswordAndUsername(email, username, password)
        }

        findViewById<Button>(R.id.signup_btn_already_registered).setOnClickListener() {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}