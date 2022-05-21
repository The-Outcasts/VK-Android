package com.theoutcasts.app

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract.CalendarCache.URI
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import layout.CommentAdapter

class PublicationActivity : AppCompatActivity() {
    private lateinit var title: String
    private lateinit var description: String
    private val likes: TextView by lazy { findViewById(R.id.likeCount) }
    private val picture: ImageView by lazy {findViewById(R.id.publication_image)}
    private  var likeCount = 1
    private val usersLiked = mutableListOf(0, 1)
    private val userId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publication)

        val rv: RecyclerView = findViewById(R.id.comments__rv)
        rv.adapter = CommentAdapter(generateCommentList())
        rv.layoutManager = LinearLayoutManager(this)

        likes.text = "Likes: $likeCount"
        picture.setImageResource(R.drawable.house)
    }
    // Список комментариев для адапрета, нужно получить из БД
    fun generateCommentList(): List<Comm> {
        return listOf(
            Comm(author = "Nick", text = "Niceasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"),
            Comm(author = "Nick", text = "Niceasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"),
            Comm(author = "Nick", text = "Niceasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"),
            Comm(author = "Nick", text = "Niceasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasd"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),
            Comm(author = "Nick", text = "Nice"),

            Comm(author = "Nick", text = "Nice"))

    }

    fun setLike(view: View) {
        if (usersLiked.contains(userId)) {
            likeCount -= 1
            likes.text = "Likes: $likeCount"
            usersLiked.remove(userId)
        } else {
            likeCount += 1
            likes.text = "Likes: $likeCount"
            usersLiked.add(userId)
        }
    }
}