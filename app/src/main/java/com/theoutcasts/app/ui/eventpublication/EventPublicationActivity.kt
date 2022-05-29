package com.theoutcasts.app.ui.eventpublication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_publication)

        mViewModel = ViewModelProvider(
            this, EventPublicationViewModelFactory())[EventPublicationViewModel::class.java]

        replaceFragment(ContentFragment.newInstance(mEventId),
                        R.id.fl_fragment_container)

        mButtonLike.setOnClickListener {
            mViewModel.likePublication()
            findViewById<ImageButton>(R.id.btn_like).setImageBitmap(
                ContextCompat.getDrawable(this,R.drawable.icon_heart_filled_96)!!.toBitmap()
            )
        }

        mButtonComment.setOnClickListener {
            replaceFragment(CommentsFragment(), R.id.fl_fragment_container)
        }

        mButtonWatchPublication.setOnClickListener {
            replaceFragment(ContentFragment(mEventId), R.id.fl_fragment_container)
        }
    }

    private fun replaceFragment(fragment: Fragment, containerViewId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment)
            .commit()
    }
}
