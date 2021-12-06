package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_searched_user_layout.view.*
import kotlinx.android.synthetic.main.item_searched_user_layout.view.tw_username
import kotlinx.android.synthetic.main.item_user_layout.view.*

class SearchedUserAdapter(val context: Context): RecyclerView.Adapter<SearchedUserAdapter.UserViewHolder>() {

    var userArrList = ArrayList<User>()
    var mItemActionListener: ItemActionListener? = null


    fun setDataForAdapter(userArrList: ArrayList<User>){
        this.userArrList = userArrList
        notifyDataSetChanged()
    }

    fun setItemActionListener(listener: ItemActionListener){
        this.mItemActionListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userArrList.get(position)
        holder.setItemData(user)
    }

    override fun getItemCount(): Int {
        return userArrList.size
    }

    inner class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        fun setItemData(user: User){
            Picasso.get().load(user.avatar_url).into(itemView.image_profile)
            itemView.full_name_profile.text = user.user_name
            itemView.user_email_profile.text = user.email
            setAction()
        }

        fun setAction() {
            itemView.user_layout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = absoluteAdapterPosition
            when(v){
                (itemView.user_layout) ->{
                    val userId = userArrList.get(position).uid!!
                    mItemActionListener?.clickItem(userId)
                }
            }
        }
    }

    interface ItemActionListener{
        fun clickItem(userId:String)
    }
}