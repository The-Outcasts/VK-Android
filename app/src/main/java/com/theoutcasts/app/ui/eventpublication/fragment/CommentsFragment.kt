package com.theoutcasts.app.ui.eventpublication.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theoutcasts.app.R
import com.theoutcasts.app.ui.eventpublication.CommentAdapter
import com.theoutcasts.app.ui.eventpublication.vm.EventPublicationViewModel


class CommentsFragment : Fragment() {
    private val mViewModel: EventPublicationViewModel by activityViewModels()

    private lateinit var mCommentRv: RecyclerView
    private lateinit var mButtonPostComment: ImageButton
    private lateinit var mCommentEditText: EditText

    private var mCommentAdapter: CommentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCommentRv = view.findViewById(R.id.rv_comments)
        mButtonPostComment = view.findViewById(R.id.btn_post_comment)
        mCommentEditText = view.findViewById(R.id.et_comment)

        setupButtonCallbacks()
        setupLiveDataObservers()

        mViewModel.loadComments()
    }

    private fun setupButtonCallbacks() {
        mButtonPostComment.setOnClickListener {
            if (mCommentEditText.text.isNotEmpty()) {
                val content = mCommentEditText.text.toString()
                mViewModel.postComment(content)
                mCommentEditText.setText("")
            }
        }
    }

    private fun setupLiveDataObservers() {
        mViewModel.comments.observe(activity as LifecycleOwner) { comments ->
            if (mCommentAdapter == null) {
                mCommentAdapter = CommentAdapter(comments)
                val layoutManager = LinearLayoutManager(context)
                mCommentRv.layoutManager = layoutManager
                mCommentRv.itemAnimator = DefaultItemAnimator()
                mCommentRv.adapter = mCommentAdapter
            }
        }

        mViewModel.postedCommentFlag.observe(activity as LifecycleOwner) { flag ->
            if (flag) {
                mCommentAdapter!!.notifyItemInserted(mCommentAdapter!!.itemCount - 1)
                mViewModel.postedCommentFlag.value = false
            }
        }

        mViewModel.errorMessage.observe(activity as LifecycleOwner) { e ->
            Toast.makeText(activity as Context, "Ошибка: $e", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CommentsFragment()
    }
}