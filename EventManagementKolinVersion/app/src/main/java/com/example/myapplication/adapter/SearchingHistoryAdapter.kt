package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.HistoricalSearchingText
import kotlinx.android.synthetic.main.searching_history_item.view.*

class SearchingHistoryAdapter(val context:Context): RecyclerView.Adapter<SearchingHistoryAdapter.HistoryViewHolder>() {

    private var historiesList = emptyList<HistoricalSearchingText>()
    private var mItemListener: ItemListener? = null

    fun setItemListener(listener: ItemListener){
        this.mItemListener = listener
    }
    fun setAdapterData(historyList: List<HistoricalSearchingText> ){
        this.historiesList = historyList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.searching_history_item,parent,false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historiesList.get(position)
        holder.setDataForUI(history)
    }

    override fun getItemCount(): Int {
        return historiesList.size
    }

    inner class HistoryViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        fun setDataForUI(history: HistoricalSearchingText){
            itemView.tw_searching_text.text = history.content
            setAction()
        }

        fun setAction(){
            itemView.btn_delete.setOnClickListener(this)
            itemView.tw_searching_text.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = absoluteAdapterPosition
            when(v){
                (itemView.btn_delete) ->{
                    val history = historiesList.get(position)
                    mItemListener?.deleteHistory(history)
                }
                (itemView.tw_searching_text) ->{

                    val history = historiesList.get(position)
                    mItemListener?.clickHistory(history)
                }
            }
        }
    }

    interface ItemListener{
        fun deleteHistory(history: HistoricalSearchingText)
        fun clickHistory(history: HistoricalSearchingText)
    }
}