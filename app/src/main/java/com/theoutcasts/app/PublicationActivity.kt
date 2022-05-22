package com.theoutcasts.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theoutcasts.app.data.repository.firebase.CommentRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.EventRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import com.theoutcasts.app.domain.interactor.CommentInteractor
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.domain.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PublicationActivity : AppCompatActivity() {
    private lateinit var title: String
    private lateinit var description: String
    private val likes: TextView by lazy { findViewById(R.id.likeCount) }
    private val picture: ImageView by lazy {findViewById(R.id.publication_image)}
    private val descriptionView: TextView by lazy {findViewById(R.id.desctiption)}
    private val descriptionTitleView: TextView by lazy {findViewById(R.id.title)}
    private  var likeCount = 0
    private val usersLiked = mutableListOf(0, 1)
    private val userId = 0
    private  val eventId: String = intent.getStringExtra("eventId").toString()
    private lateinit var commentList: List<Comment>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publication)
        val rv: RecyclerView = findViewById(R.id.comments__rv)
        rv.adapter = CommentAdapter(generateCommentList())
        rv.layoutManager = LinearLayoutManager(this)
        likes.text = "Likes: $likeCount"
        picture.setImageResource(R.drawable.house)
        GlobalScope.launch(Dispatchers.IO) {
            val eventInteractor = EventInteractor(EventRepositoryImpl())
            val result = eventInteractor.getById(eventId)
            result.fold(
                onSuccess = {
                    likes.text = "Likes:"+ it.likeCount.toString()
                    descriptionView.text = it.description
                    descriptionTitleView.text = it.id.toString()
                },
                onFailure = {

                }
            )
        }
    }
    // Список комментариев для адаптера, нужно получить из БД
    fun generateCommentList(): List<Comment> {
        GlobalScope.launch(Dispatchers.IO) {
            var commentInteractor = CommentInteractor(commentRepository = CommentRepositoryImpl())
            val result = commentInteractor.getByEventId(eventId)
            result.fold(
                onSuccess = {
                    commentList = it
                },
                onFailure = {}
            )
        }
        return commentList
    }

    fun setLike(view: View) {
        GlobalScope.launch(Dispatchers.IO) {
            var eventInteractor = EventInteractor(EventRepositoryImpl())
            val result = eventInteractor.getById(eventId)
            result.fold(
                onSuccess = {
                    // нужна проверка на то, что пользователь уже лайкнул(-1)
                    it.likeCount  = it.likeCount!! + 1
                    eventInteractor.save(it)
                },
                onFailure = {}
            )




        }

    }
}