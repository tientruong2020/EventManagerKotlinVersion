package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user_layout.view.*

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){
    var userArrList = ArrayList<User>()
    var mItemActionListener: ItemActionListener? = null


    fun setAdapterData(list: ArrayList<User>){
        this.userArrList = list
        notifyDataSetChanged()
    }

    fun setItemListener(listener: ItemActionListener){
        this.mItemActionListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userArrList.get(position)
        holder.setUser(user)
    }

    override fun getItemCount(): Int {
        return userArrList.size
    }

    inner class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        fun setUser(user: User){
            setAction()
            Picasso.get().load(user.avatar_url).into(itemView.image_profile)
            itemView.full_name_profile.text = user.user_name
            itemView.user_email_profile.text = user.email
        }

        fun setAction(){
           itemView.user_layout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = absoluteAdapterPosition
            when(v){
                (itemView.user_layout) -> {
                    val userid = userArrList.get(position).uid!!
                    mItemActionListener?.clickUser(userid)
                }
            }
        }
    }

    interface ItemActionListener{
        fun clickUser(uid:String)
    }
}