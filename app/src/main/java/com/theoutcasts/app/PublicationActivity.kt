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
    private val picture: ImageView by lazy { findViewById(R.id.publication_image) }
    private lateinit var pictureUrl: String
    private val descriptionView: TextView by lazy { findViewById(R.id.desctiption) }
    private val descriptionTitleView: TextView by lazy { findViewById(R.id.title) }
    private val commentCount: TextView by lazy { findViewById(R.id.commentCount) }
    lateinit var commentList: List<Comment>
    private var eventId = intent.getStringExtra("eventId")

    private val rv: RecyclerView by lazy { findViewById(R.id.comments__rv) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (eventId == null) {
            eventId = "-N2k_Yt4M_Z1-AJHpQIS"
        }
        setContentView(R.layout.activity_publication)
        generateCommentList()
        //while (!::commentList.isInitialized) { sleep(10)}
//        if (::commentList.isInitialized) {
//            rv.adapter = CommentAdapter(commentList)
//        }
        rv.layoutManager = LinearLayoutManager(this)
        picture.setImageResource(R.drawable.house)
        Thread {

        }
        GlobalScope.launch(Dispatchers.IO) {
            val eventInteractor = EventInteractor(EventRepositoryImpl())
            val result = eventInteractor.getById(eventId!!)
            result.fold(
                onSuccess = {
                    this@PublicationActivity.runOnUiThread {
                        likes.text = "Likes:" + it.likeCount.toString()
                        descriptionView.text = it.description
                        descriptionTitleView.text = it.id.toString()
                    }
                    pictureUrl = it.pictureURL!!
                },
                onFailure = {
                    Toast.makeText(this@PublicationActivity, it.toString(), Toast.LENGTH_LONG)
                }
            )
        }.invokeOnCompletion {
            GlobalScope.launch(Dispatchers.IO) {
                val imgInteractor = ImageInteractor(ImageRepositoryImpl())
                val imgRes = imgInteractor.downloadImage(pictureUrl)
                imgRes.fold(
                    onSuccess = {
                        this@PublicationActivity.runOnUiThread {
                            picture.setImageBitmap(it)
                            descriptionTitleView.text = "image"
                        }
                    },
                    onFailure = {
                    }
                )
            }
        }
    }

    // Список комментариев для адаптера, нужно получить из БД
    fun generateCommentList() {
        GlobalScope.launch(Dispatchers.IO) {
            var commentInteractor = CommentInteractor(commentRepository = CommentRepositoryImpl())
            val result = commentInteractor.getByEventId(eventId!!)
            result.fold(
                onSuccess = {
                    commentList = it
                    this@PublicationActivity.runOnUiThread {
                        commentCount.text = it.size.toString()
                        rv.adapter = CommentAdapter(commentList)
                    }
                },
                onFailure = {}
            )
        }
    }

    fun setLike(view: View) {
        GlobalScope.launch(Dispatchers.IO) {
            var eventInteractor = EventInteractor(EventRepositoryImpl())
            val result = eventInteractor.getById(eventId!!)
            result.fold(
                onSuccess = {
                    // нужна проверка на то, что пользователь уже лайкнул(-1)
                    this@PublicationActivity.runOnUiThread{
                        it.likeCount = it.likeCount!! + 1
                    }
                    eventInteractor.save(it)
                },
                onFailure = {}
            )
        }

    }
}