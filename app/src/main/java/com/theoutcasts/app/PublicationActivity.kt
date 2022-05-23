package com.theoutcasts.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theoutcasts.app.data.repository.firebase.CommentRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.EventRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.ImageRepositoryImpl
import com.theoutcasts.app.data.repository.firebase.UserRepositoryImpl
import com.theoutcasts.app.domain.interactor.CommentInteractor
import com.theoutcasts.app.domain.interactor.EventInteractor
import com.theoutcasts.app.domain.interactor.ImageInteractor
import com.theoutcasts.app.domain.interactor.UserInteractor
import com.theoutcasts.app.domain.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

class PublicationActivity : AppCompatActivity() {
    private lateinit var title: String
    private lateinit var description: String
    private val likes: TextView by lazy { findViewById(R.id.likeCount) }
    private val picture: ImageView by lazy {findViewById(R.id.publication_image)}
    private lateinit var pictureUrl: String
    private val descriptionView: TextView by lazy {findViewById(R.id.desctiption)}
    private val descriptionTitleView: TextView by lazy {findViewById(R.id.title)}
    lateinit var commentList: List<Comment>
    private val eventId: String = "-N28Qa_-cM_aSyKqRe6P"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publication)
        generateCommentList()
        val rv: RecyclerView = findViewById(R.id.comments__rv)
        if (::commentList.isInitialized) {
            rv.adapter = CommentAdapter(commentList)
        }
        rv.layoutManager = LinearLayoutManager(this)
        picture.setImageResource(R.drawable.house)

        GlobalScope.launch(Dispatchers.Main) {
            val eventInteractor = EventInteractor(EventRepositoryImpl())
            val result = eventInteractor.getById(eventId)
            result.fold(
                onSuccess = {
                    // TODO загрузка фото по ur
                    likes.text = "Likes:"+ it.likeCount.toString()
                    descriptionView.text = it.description
                    descriptionTitleView.text = it.id.toString()
                    pictureUrl = it.pictureURL!!
                },
                onFailure = {
                    Toast.makeText(this@PublicationActivity, it.toString(), Toast.LENGTH_LONG)
                }
            )
        }.invokeOnCompletion {
            GlobalScope.launch(Dispatchers.Main) {
                val imgInteractor = ImageInteractor(ImageRepositoryImpl())
                val imgRes = imgInteractor.downloadImage(pictureUrl)
                imgRes.fold(
                    onSuccess = {
                            picture.setImageBitmap(it)
                    },
                    onFailure = {}
                )
            }
        }
    }
    // Список комментариев для адаптера, нужно получить из БД
    fun generateCommentList() {
        lateinit var comList: List<Comment>
        GlobalScope.launch(Dispatchers.Main) {
            var commentInteractor = CommentInteractor(commentRepository = CommentRepositoryImpl())
            val result = commentInteractor.getByEventId(eventId)
            result.fold(
                onSuccess = {
                    descriptionView.text = "success"
                    },
                onFailure = {}
            )
        }
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