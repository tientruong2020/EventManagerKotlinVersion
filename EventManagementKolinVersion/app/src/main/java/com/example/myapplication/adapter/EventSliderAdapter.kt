package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.myapplication.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.each_event_image_layout.view.*

class EventSliderAdapter(val context: Context):PagerAdapter() {

    var imgUriList = ArrayList<String>()

    fun setDataImgList(imgUriList: ArrayList<String>){
        this.imgUriList = imgUriList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return imgUriList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.each_event_image_layout, container, false)
        val imgUri = imgUriList.get(position)
        Picasso.get().load(imgUri).into(view.ivEventImage)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}