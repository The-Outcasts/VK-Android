package com.theoutcasts.app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.MainActivity
import com.theoutcasts.app.MainActivityTest
import com.theoutcasts.app.R
import com.theoutcasts.app.domain.interactor.InvalidLoginOrPasswordException
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import com.theoutcasts.app.ui.vm.SignInViewModel
import com.theoutcasts.app.ui.vm.factory.SignInViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SignInActivity : AppCompatActivity() {
    private lateinit var vm: SignInViewModel

    private val emailEditText: EditText by lazy { findViewById(R.id.signin_et_email) }
    private val passwordEditText: EditText by lazy { findViewById(R.id.signin_et_password) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        vm = ViewModelProvider(this, SignInViewModelFactory())[SignInViewModel::class.java]

        vm.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this@SignInActivity, errorMessage, Toast.LENGTH_LONG).show()
        })

        vm.signedUser.observe(this, Observer { user ->
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.putExtra("current_user_id", user.id)
            intent.putExtra("current_user_email", user.email)
            intent.putExtra("current_user_username", user.username)
            startActivity(intent)
            finish()
        })

        findViewById<Button>(R.id.signin_btn_login).setOnClickListener() {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            vm.signInWithEmailAndPassword(email, password)
        }

        findViewById<Button>(R.id.signup_btn_already_registered).setOnClickListener() {
            val intent = Intent(this@SignInActivity, MainActivityTest::class.java)
            startActivity(intent)
            finish()
        }
    }
}