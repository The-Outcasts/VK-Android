package com.theoutcasts.app.ui.eventpublication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.theoutcasts.app.R
import com.theoutcasts.app.ui.eventpublication.fragment.CommentsFragment
import com.theoutcasts.app.ui.eventpublication.fragment.ContentFragment
import com.theoutcasts.app.ui.eventpublication.vm.EventPublicationViewModel
import com.theoutcasts.app.ui.eventpublication.vm.EventPublicationViewModelFactory


class EventPublicationActivity : AppCompatActivity() {
    private lateinit var mViewModel: EventPublicationViewModel

    private val mEventId: String by lazy { intent.getStringExtra("EVENT_ID")!! }

    private val mButtonComment: ImageButton by lazy { findViewById(R.id.btn_show_comments) }
    private val mButtonWatchPublication: ImageButton by lazy { findViewById(R.id.btn_show_publication) }
    private val mButtonLike: ImageButton by lazy { findViewById(R.id.btn_like) }
    private val mCommentCount: TextView by lazy { findViewById(R.id.tv_comment_count) }
    private val mLikeCount: TextView by lazy { findViewById(R.id.tv_like_count) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_publication)

        mViewModel = ViewModelProvider(
            this, EventPublicationViewModelFactory())[EventPublicationViewModel::class.java]

        replaceFragment(ContentFragment.newInstance(mEventId), R.id.fl_fragment_container)

        setupButtonCallbacks()
        setupLiveDataObservers()
    }

    private fun setupButtonCallbacks() {
        mButtonLike.setOnClickListener {
            mViewModel.likePublication()
        }

        mButtonComment.setOnClickListener {
            replaceFragment(CommentsFragment.newInstance(), R.id.fl_fragment_container)
        }

        mButtonWatchPublication.setOnClickListener {
            replaceFragment(ContentFragment(mEventId), R.id.fl_fragment_container)
        }
    }

    private fun setupLiveDataObservers() {
        mViewModel.commentCount.observe(this) { commentCount ->
            mCommentCount.text = commentCount.toString()
        }

        mViewModel.likeCount.observe(this) { likeCount ->
            mLikeCount.text = likeCount.toString()
        }

        mViewModel.isCurrentUserLikedEvent.observe(this) { isLiked ->
            if (isLiked) {
                mButtonLike.setImageBitmap(ContextCompat
                    .getDrawable(this, R.drawable.icon_heart_filled_96)!!
                    .toBitmap()
                )
            } else {
                mButtonLike.setImageBitmap(ContextCompat
                    .getDrawable(this, R.drawable.icon_heart_empty_80)!!
                    .toBitmap()
                )
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, containerViewId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment)
            .commit()
    }
}
