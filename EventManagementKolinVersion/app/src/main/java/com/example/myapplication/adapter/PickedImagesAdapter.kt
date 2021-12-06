package com.example.myapplication.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.item_picked_image_layout.view.*

class PickedImagesAdapter(val context:Context): RecyclerView.Adapter<PickedImagesAdapter.PickedImageViewHolder>() {
    var pickedImagesArrList = ArrayList<Uri>()

    var mClickListener: ClickListener? = null

    fun setData(pickedImagesList :ArrayList<Uri>){
        this.pickedImagesArrList = pickedImagesList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickedImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_picked_image_layout, parent, false)
        return PickedImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PickedImageViewHolder, position: Int) {
        val imgUri: Uri = pickedImagesArrList.get(position)
        holder.setDataForUi(imgUri)
    }

    override fun getItemCount(): Int {
        return pickedImagesArrList.size
    }

    inner class PickedImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        fun setDataForUi(imageUri:Uri){
            itemView.img_photo.setImageURI(imageUri)
            itemView.btn_delete_img.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            when(v?.id){
                (R.id.btn_delete_img)->{
                    mClickListener?.clickRemove(position)
                }
            }
        }
    }

    fun setClickListener(listener : ClickListener){
        this.mClickListener = listener
    }

    interface ClickListener{
        fun clickRemove(position: Int)
    }
}