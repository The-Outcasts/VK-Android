package com.theoutcasts.app.ui.eventpublication.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.theoutcasts.app.R
import com.theoutcasts.app.ui.eventpublication.vm.EventPublicationViewModel


class ContentFragment(private val mEventId: String) : Fragment() {
    private val mViewModel: EventPublicationViewModel by activityViewModels()

    private lateinit var mEventDescriptionTextView: TextView
    private lateinit var mEventPictureImageView: ImageView
    private lateinit var mProgressBar: ProgressBar

    // 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.loadEvent(mEventId)
    }

    // 2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    // 3
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mEventDescriptionTextView = view.findViewById<TextView>(R.id.tv_event_description)
        mEventPictureImageView = view.findViewById(R.id.iv_event_image)
        mProgressBar = view.findViewById(R.id.pb_loading_image)

        mViewModel.eventDescription.observe(activity as LifecycleOwner) { description ->
            mEventDescriptionTextView.text = description
        }

        mViewModel.eventPicture.observe(activity as LifecycleOwner) { bitmap ->
            mEventPictureImageView.setImageBitmap(bitmap)
            mProgressBar.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(eventId: String) = ContentFragment(eventId)
    }
}
