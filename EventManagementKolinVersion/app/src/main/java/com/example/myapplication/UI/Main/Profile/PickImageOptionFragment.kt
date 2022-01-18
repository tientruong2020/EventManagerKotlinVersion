package com.example.myapplication.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_pick_image_option.*

class PickImageOptionFragment : DialogFragment(), View.OnClickListener {
    private var mOnClickListener : onClickListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_image_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pick_gallery_layout.setOnClickListener(this)
        pick_camera_layout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            (R.id.pick_gallery_layout) ->{
                mOnClickListener?.onClickOpenGallery()
            }
            (R.id.pick_camera_layout) -> {
                mOnClickListener?.onClickOpenCamera()
            }
        }
    }

    fun setDialogChooseOptionListener(listener: onClickListener){
        this.mOnClickListener = listener
    }

    interface onClickListener{
        fun onClickOpenGallery()
        fun onClickOpenCamera()
    }

}