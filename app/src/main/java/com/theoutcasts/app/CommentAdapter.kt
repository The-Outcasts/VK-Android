package layout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theoutcasts.app.Comm
import com.theoutcasts.app.R

class CommentAdapter(
    private val comments: List<Comm>,
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.commentsection, null)
        return CommentViewHolder(view)
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int {
        return comments.size
    }
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author: TextView = itemView.findViewById(R.id.author)
        val comment: TextView = itemView.findViewById(R.id.comment_text)
        fun bind(com: Comm) {
            (com.author + ":").also { author.text = it }
            comment.text = com.text
        }
    }
}