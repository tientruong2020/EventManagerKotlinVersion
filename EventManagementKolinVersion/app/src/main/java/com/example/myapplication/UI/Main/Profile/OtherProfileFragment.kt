package com.example.myapplication.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.MyEventAdapter
import com.example.myapplication.databinding.FragmentOtherProfileBinding
import com.example.myapplication.viewModel.OtherProfileViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception

class OtherProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var uid: String
    private lateinit var viewBinding: FragmentOtherProfileBinding
    private lateinit var otherProfileViewModel: OtherProfileViewModel
    private lateinit var myEventAdapter: MyEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val arg = OtherProfileFragmentArgs.fromBundle(it)
            uid = arg.uid
        }
        otherProfileViewModel = ViewModelProvider(this).get(OtherProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentOtherProfileBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        setAction()
        buildRecyclerView()
    }

    private fun setAction() {
        viewBinding.btnFlow.setOnClickListener(this)
        viewBinding.iwEmail.setOnClickListener(this)
        viewBinding.twEmail.setOnClickListener(this)
    }

    private fun setData() {
        otherProfileViewModel.getUserById(uid).observe(viewLifecycleOwner, {
            if (it.avatar_url != "") {
                Picasso.get().load(it.avatar_url).into(viewBinding.imvAvatar)
            } else {
                viewBinding.imvAvatar.setImageResource(R.drawable.defaultavatar)
            }
            viewBinding.twEmail.text = it.email
            viewBinding.twUsername.text = it.user_name
            viewBinding.twBio.text = it.bio
        })
        getFollowStatus()
        countFollower()
        countFollowing()
    }

    private fun countFollower(){
        otherProfileViewModel.countFollower(uid).observe(viewLifecycleOwner,{
            viewBinding.twFollowerNumber.text = it.toString()
        })
    }

    private fun countFollowing(){
        otherProfileViewModel.countFollowing(uid).observe(viewLifecycleOwner,{
            viewBinding.twFollowingNumber.text = it.toString()
        })
    }

    private fun getFollowStatus() {
        otherProfileViewModel.isFollowed(uid).observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.btnFlow.text = "Unfollow"
            } else {
                viewBinding.btnFlow.text = "Follow"
            }
        })
    }

    private fun buildRecyclerView() {
        myEventAdapter = MyEventAdapter()
        viewBinding.rcvMyEvent.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.rcvMyEvent.adapter = myEventAdapter
        otherProfileViewModel.getEventsByUid(uid).observe(viewLifecycleOwner, {
            myEventAdapter.setAdapterData(it)
            viewBinding.twEventNumber.text = it.size.toString()
        })
        myEventAdapter.setItemActionListener(object : MyEventAdapter.ItemActionListener {
            override fun clickItemListener(eventId: String) {
                val action =
                    OtherProfileFragmentDirections.actionOtherProfileFragmentToDetailEventFragment(
                        eventId
                    )
                findNavController().navigate(action)
            }

        })
    }
    fun openEmail(){
        val recipient = viewBinding.twEmail.text
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient.toString()))

        try {
            startActivity(Intent.createChooser(intent,"Choose Email Client"))
        }
        catch (e: Exception){
            Log.d("open_email_exception",e.message.toString())
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            (viewBinding.btnFlow) -> {
                otherProfileViewModel.followOrUnfollow(uid)
                otherProfileViewModel.resultHandleFollow.observe(viewLifecycleOwner, {
                    if (it) {
                        getFollowStatus()
                    }
                })
            }
            (viewBinding.twEmail) -> {
                openEmail()
            }
            (viewBinding.iwEmail) -> {
                openEmail()
            }
        }
    }

}