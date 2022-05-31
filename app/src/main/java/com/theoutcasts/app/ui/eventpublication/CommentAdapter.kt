package com.theoutcasts.app.ui.eventpublication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theoutcasts.app.R
import com.theoutcasts.app.domain.model.Comment

class CommentAdapter(
    private var mComments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.authorUsernameTextView.text = mComments[position].username
        holder.contentTextView.text = mComments[position].content
    }

    override fun getItemCount(): Int = mComments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorPicImageView = itemView.findViewById<ImageView>(R.id.iv_author_pic)
        val authorUsernameTextView = itemView.findViewById<TextView>(R.id.tv_author_username)
        val contentTextView = itemView.findViewById<TextView>(R.id.tv_content)
    }

}