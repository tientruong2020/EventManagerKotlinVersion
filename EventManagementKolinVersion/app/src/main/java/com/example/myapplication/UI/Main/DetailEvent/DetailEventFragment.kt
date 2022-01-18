package com.example.myapplication.ui.main.detailevent

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.ui.main.home.CommentValidator
import com.example.myapplication.ui.main.qrcodegenerator.QRCodeGeneratorFragment
import com.example.myapplication.adapter.CommentsAdapter
import com.example.myapplication.adapter.EventSliderAdapter
import com.example.myapplication.databinding.FragmentDetailEventBinding
import com.example.myapplication.viewModel.DetailEventViewModel
import com.squareup.picasso.Picasso


class DetailEventFragment : Fragment(), View.OnClickListener {

    private lateinit var detailEventViewModel: DetailEventViewModel
    private lateinit var eventId:String
    private lateinit var viewBinding: FragmentDetailEventBinding
    private lateinit var commentsAdapter : CommentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val arg = DetailEventFragmentArgs.fromBundle(it)
            eventId = arg.eventId
        }
        detailEventViewModel = ViewModelProvider(this).get(DetailEventViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentDetailEventBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentUser()
        setAction()
        getEvent()
        buildCommentRecyclerView()
        getComment()
    }

    private fun setAction(){
        viewBinding.ivQRCode.setOnClickListener(this)
        viewBinding.btnBackToHomeFragment.setOnClickListener(this)
        viewBinding.btnSendComment.setOnClickListener(this)
        viewBinding.ivDropLike.setOnClickListener(this)
        viewBinding.civUserProfileImage.setOnClickListener(this)
    }


    private fun getEvent(){
        detailEventViewModel.getEventById(eventId).observe(viewLifecycleOwner,{ event ->
            if (event != null){
                viewBinding.txtUserFullName.text = event.creator!!.user_name
                buildImgSlider(event.imagesList)
                if (event.isLiked){
                    viewBinding.ivDropLike.setImageResource(R.drawable.like)
                }
                else{
                    viewBinding.ivDropLike.setImageResource(R.drawable.dislike)
                }
                viewBinding.twEventName.text = event.eventName
                Picasso.get().load(Uri.parse(event.creator!!.avatar_url)).into(viewBinding.civUserProfileImage)
                viewBinding.txtUserFullName.text = event.creator!!.user_name
                viewBinding.txtEventPlace.text = event.placeOrUrl
                viewBinding.txtEventStartDate.text = event.startDate
                viewBinding.txtEventEndDate.text = event.endDate
                viewBinding.civUserProfileImage.setOnClickListener { toUserProFile(event.uid) }
            }
        })
    }
    private fun toUserProFile(userId: String){
        val currentUid  = detailEventViewModel.getCurrentUid()
        if (userId.equals(currentUid)){
            findNavController().navigate(R.id.profileFragment)
        }
        else{
            val action = DetailEventFragmentDirections.actionDetailEventFragmentToOtherProfileFragment(userId)
            findNavController().navigate(action)
        }
    }

    private fun getCurrentUser(){
        detailEventViewModel.getCurrentUser().observe(viewLifecycleOwner,{
            Picasso.get().load(it.avatar_url).into(viewBinding.imvAvatar)
        })
    }

    fun getComment(){
        detailEventViewModel.getAllComment(eventId).observe(viewLifecycleOwner, {
            if (it.size > 0){
                Log.d("checkCommentSize", it.size.toString())
                commentsAdapter.setDataForAdapter(it)
            }
        })
    }

    fun buildCommentRecyclerView(){
        commentsAdapter = CommentsAdapter(this.requireContext())
        viewBinding.rcvComments.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
        viewBinding.rcvComments.adapter = commentsAdapter
    }
    fun buildImgSlider(imgUriList:ArrayList<String>){
        val eventSliderAdapter = EventSliderAdapter(requireContext())
        eventSliderAdapter.setDataImgList(imgUriList)
        viewBinding.viewpager.adapter = eventSliderAdapter
        viewBinding.circleIndicator.setViewPager(viewBinding.viewpager)
        eventSliderAdapter.registerDataSetObserver(viewBinding.circleIndicator.dataSetObserver)
        if (imgUriList.size == 1){
            viewBinding.circleIndicator.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when(v){
            (viewBinding.ivQRCode) ->{
                toQRCodeGenarator()
            }
            (viewBinding.btnBackToHomeFragment) ->{
                val action = DetailEventFragmentDirections.actionDetailEventFragmentToHomeFragment()
                findNavController().navigate(action)
            }
            (viewBinding.btnSendComment) -> {
                sendComment()
            }
            ( viewBinding.ivDropLike) -> {
                detailEventViewModel.handleLikeOrDislikeEvent(eventId)
            }
        }
    }

    fun toQRCodeGenarator(){
        val bundle = Bundle()
        bundle.putString("event_id",eventId)
        val qrCodeGeneratorDialog = QRCodeGeneratorFragment()
        activity?.supportFragmentManager?.let {
            qrCodeGeneratorDialog.arguments = bundle
            qrCodeGeneratorDialog.show(it, QRCodeGeneratorFragment::class.java.name)
        }

    }
    private fun sendComment(){
        val content = viewBinding.etComment.text.toString()
        if (CommentValidator.isEmptyContent(content)){
            Toast.makeText(requireContext(), "Please!! Write comment before sending these.", Toast.LENGTH_SHORT).show()
        }
        else{
            viewBinding.btnSendComment.isClickable = false
            detailEventViewModel.addComment(content, eventId)
            detailEventViewModel.resultAddedComment?.observe(viewLifecycleOwner,{
                if (it){
                    viewBinding.etComment.text?.clear()
                    viewBinding.btnSendComment.isClickable = true
                }
            })
        }
    }

}