package com.example.myapplication.ui.main.home

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
import com.example.myapplication.adapter.CommentsAdapter
import com.example.myapplication.databinding.FragmentCommentBinding
import com.example.myapplication.model.Comment
import com.example.myapplication.viewModel.CommentViewModel
import com.squareup.picasso.Picasso


class CommentFragment : Fragment(), View.OnClickListener {

    private lateinit var commentViewModel: CommentViewModel
    private lateinit var viewBinding : FragmentCommentBinding
    private lateinit var eventId:String
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           val arg = CommentFragmentArgs.fromBundle(it)
            eventId = arg.eventId
            Log.d("EventIdCommentFrag", eventId)
        }
        initViewModel()
    }

    private fun initViewModel(){
        commentViewModel = ViewModelProvider(this).get(CommentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCommentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentUser()
        buildCommentRecyclerView()
        displayAllComment()
        setAction()
    }

    private fun getCurrentUser(){
        commentViewModel.getCurentUser().observe(viewLifecycleOwner,{ currentUser ->
            if (currentUser != null){
                Picasso.get().load(currentUser.avatar_url).into(viewBinding.imvAvatar)
            }
        })
    }

    private fun sendComment(){
        val content = viewBinding.etComment.text.toString()
        if (CommentValidator.isEmptyContent(content)){
            Toast.makeText(requireContext(), "Please!! Write comment before sending these.", Toast.LENGTH_SHORT).show()
        }
        else{
            viewBinding.btnSendComment.isClickable = false
            commentViewModel.addComment(content, eventId)
            commentViewModel.resultAddedComment?.observe(viewLifecycleOwner,{
                if (it){
                    viewBinding.etComment.text?.clear()
                    viewBinding.btnSendComment.isClickable = true
                }
            })
        }
    }

    fun setAction(){
        viewBinding.btnSendComment.setOnClickListener(this)
        viewBinding.btnBackToHomeFragment.setOnClickListener(this)
    }

    fun displayAllComment(){
        commentViewModel.getAllComment(eventId).observe(viewLifecycleOwner, {
           if (it.size > 0){
                val comparator = Comparator { c1:Comment, c2:Comment -> c2.createdAt.compareTo(c1.createdAt)  }
               commentsAdapter.setDataForAdapter(it.sortedWith(comparator))
           }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            (R.id.btn_send_comment) -> {
                sendComment()
            }
            (R.id.btn_back_to_home_fragment) ->{
                backToHome()
            }
        }
    }

    fun backToHome(){
        val action = CommentFragmentDirections.actionCommentFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    fun buildCommentRecyclerView(){
        commentsAdapter = CommentsAdapter(this.requireContext())
        viewBinding.rcvComments.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
        viewBinding.rcvComments.adapter = commentsAdapter
        commentsAdapter.setItemListener(object : CommentsAdapter.ItemActionListener{
            override fun clickAvatar(userId: String) {
                toUserProFile(userId)
            }

        })
    }
    private fun toUserProFile(userId: String){
        val currentUid  = commentViewModel.getCurrentUid()
        Log.d("currentUid","$currentUid  & $userId")
        if (userId.equals(currentUid)){
            findNavController().navigate(R.id.profileFragment)
        }
        else{
            val action = CommentFragmentDirections.actionCommentFragmentToOtherProfileFragment(userId)
            findNavController().navigate(action)
        }
    }


}