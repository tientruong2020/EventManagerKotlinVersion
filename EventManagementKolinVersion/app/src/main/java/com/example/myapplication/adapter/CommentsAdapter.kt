package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Comment
import com.example.myapplication.viewModel.CommentViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.each_comment_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommentsAdapter(val context: Context): RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    var commentArrList = emptyList<Comment>()
    var mItemListener: ItemActionListener? = null

    fun setDataForAdapter(commentArrList: List<Comment>){
        this.commentArrList = commentArrList
        notifyDataSetChanged()
    }

    fun setItemListener(listener: ItemActionListener){
        this.mItemListener = listener
    }
    inner class CommentViewHolder(itemView:View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        fun setDataForUi(comment:Comment){
            setAction()
            Picasso.get().load(comment.user!!.avatar_url).into(itemView.civComentUserImage)
            itemView.txtCommentedUserFullName.text = comment.user!!.user_name
            itemView.txtCommentedContent.text = comment.content
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm")
            val dateStr = simpleDateFormat.format(Date(comment.createdAt))
            itemView.txtCommentedTime.text = dateStr

        }

        fun setAction(){
            itemView.civComentUserImage.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            when(v){
                (itemView.civComentUserImage) -> {
                    val uid = commentArrList.get(position).uid
                    mItemListener?.clickAvatar(uid)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.each_comment_layout, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentArrList.get(position)
        holder.setDataForUi(comment)
    }

    override fun getItemCount(): Int {
        return commentArrList.size
    }

    interface ItemActionListener{
        fun clickAvatar(userId:String)
    }
}