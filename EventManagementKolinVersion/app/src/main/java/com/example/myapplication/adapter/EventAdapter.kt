package com.example.myapplication.adapter


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Event
import com.example.myapplication.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.each_event_layout.view.*
import kotlinx.android.synthetic.main.fragment_other_profile.view.*

class EventAdapter(val context:Context):RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    var eventList = emptyList<Event>()

    var mItemActionListener: ItemActionListener? =null

    fun setEventData(eventList : List<Event>){
        this.eventList = eventList
        notifyDataSetChanged()
    }

    fun setItemActionListener(listener:ItemActionListener){
        this.mItemActionListener = listener
    }

    inner class EventViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        fun setDataForItem(event:Event, user: User){
            setAction()
            itemView.tw_event_name.text = event.eventName
            Picasso.get().load(Uri.parse(user.avatar_url)).into(itemView.civUserProfileImage)
            itemView.txtUserFullName.text = user.user_name
            itemView.txtEventPlace.text = event.placeOrUrl
            itemView.txtEventStartDate.text = event.startDate
            itemView.txtEventEndDate.text = event.endDate
            buildImgSlider(event.imagesList)
            checkLikedEvent(event.isLiked)
        }

        fun checkLikedEvent(isLiked : Boolean){
            if (isLiked){
                itemView.ivDropLike.setImageResource(R.drawable.like)
            }
            else{
                itemView.ivDropLike.setImageResource(R.drawable.dislike)
            }
        }

        fun setAction(){
            itemView.ivDropLike.setOnClickListener(this)
            itemView.ivComment.setOnClickListener(this)
            itemView.event_layout.setOnClickListener(this)
            itemView.ivQRCode.setOnClickListener(this)
            itemView.civUserProfileImage.setOnClickListener(this)
            itemView.txtUserFullName.setOnClickListener(this)
            itemView.ivJoinEvent.setOnClickListener(this)
        }

        fun buildImgSlider(imgUriList:ArrayList<String>){
            val eventSliderAdapter = EventSliderAdapter(context)
            eventSliderAdapter.setDataImgList(imgUriList)
            itemView.viewpager.adapter = eventSliderAdapter
            itemView.circle_indicator.setViewPager(itemView.viewpager)
            eventSliderAdapter.registerDataSetObserver(itemView.circle_indicator.dataSetObserver)
            if (imgUriList.size == 1){
                itemView.circle_indicator.visibility = View.GONE
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            var isLiked = eventList.get(position).isLiked
            if (position != RecyclerView.NO_POSITION){
                when(v) {
                    (itemView.ivDropLike) -> {
                        isLiked = !isLiked
                        checkLikedEvent(isLiked)
                        val eventId = eventList.get(position).id
                        mItemActionListener?.clickLikeOrDislikeListener(eventId, position)
                    }
                    (itemView.ivComment) -> {
                        val eventId = eventList.get(position).id
                        mItemActionListener?.clickCommentsIcon((eventId))
                    }
                    (itemView.event_layout) ->{
                        val eventId = eventList.get(position).id
                        mItemActionListener?.clickEvent(eventId)
                    }
                    (itemView.ivQRCode) ->{
                        val eventId = eventList.get(position).id
                        mItemActionListener?.clickQRCode(eventId)
                    }
                    (itemView.civUserProfileImage) ->{
                        val userId = eventList.get(position).uid
                        mItemActionListener?.clickAvatar(userId)
                    }
                    (itemView.txtUserFullName) ->{
                        val userId = eventList.get(position).uid
                        mItemActionListener?.clickUserName(userId)
                    }
                    (itemView.ivJoinEvent) -> {
                        val eventId = eventList.get(position).id
                        mItemActionListener?.clickJoinEvent(eventId)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.each_event_layout,parent,false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventItem = eventList.get(position)
        val user:User = eventItem.creator!!
        holder.setDataForItem(eventItem,user)

    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    interface ItemActionListener{
        fun clickLikeOrDislikeListener(eventId:String, position: Int)
        fun clickCommentsIcon(eventId: String)
        fun clickEvent(eventId:String)
        fun clickQRCode(eventId: String)
        fun clickAvatar(userId:String)
        fun clickUserName(userId: String)
        fun clickJoinEvent(eventId : String)
    }

}