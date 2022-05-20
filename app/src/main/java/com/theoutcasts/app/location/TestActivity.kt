package com.theoutcasts.app.location

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.theoutcasts.app.MainActivity
import com.theoutcasts.app.R

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun loadPublication(view: View) {
        val publicate = Intent(this, MainActivity::class.java)
        startActivity(publicate)
    }
}