package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Event
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_event_layout.view.*

class MyEventAdapter:RecyclerView.Adapter<MyEventAdapter.MyEventViewHolder>() {

    var myEventArrList = ArrayList<Event>()
    var mItemActionListener: ItemActionListener? = null

    fun setItemActionListener(listener: ItemActionListener){
        this.mItemActionListener = listener
    }
    fun setAdapterData(list : ArrayList<Event>){
        this.myEventArrList = list
        notifyDataSetChanged()
    }
    fun addEvents(eventList:ArrayList<Event>){
        this.myEventArrList.addAll(eventList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_event_layout, parent, false)
        return MyEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyEventViewHolder, position: Int) {
        val event = myEventArrList.get(position)
        holder.setDataForUI(event)
    }

    override fun getItemCount(): Int {
        return myEventArrList.size
    }

    inner class MyEventViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        @SuppressLint("SetTextI18n")
        fun setDataForUI(event: Event){
            Picasso.get().load(event.imagesList[0]).into(itemView.civSearchEventImage)
            itemView.txtSearchEventName.text = event.eventName
            itemView.txtSearchEventDescription.text = event.description
            itemView.txtCalendarEventDate.text = event.endDate +" - "+ event.startDate
            setAction()
        }

        fun setAction(){
            itemView.itemview_layout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = absoluteAdapterPosition
            when(v){
                (itemView.itemview_layout) -> {
                    val eventId = myEventArrList.get(position).id
                    mItemActionListener?.clickItemListener(eventId)
                }
            }
        }
    }

    interface ItemActionListener{
        fun clickItemListener(eventId:String)
    }
}