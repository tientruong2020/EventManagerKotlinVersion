package com.example.myapplication.ui.main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.ui.main.qrcodegenerator.QRCodeGeneratorFragment
import com.example.myapplication.adapter.EventAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.Event
import com.example.myapplication.viewModel.HomeViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class HomeFragment : Fragment(),View.OnClickListener {
    lateinit var homeViewModel: HomeViewModel
    lateinit var eventAdapter: EventAdapter
    lateinit var viewBinding: FragmentHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var eventArrList: ArrayList<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        eventArrList = ArrayList<Event>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentHomeBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAction()
        getCurrentUser()
        buildEventRecyclerView()
        displayAllEvent()
    }

    fun setAction(){
        viewBinding.civAvatar.setOnClickListener(this)
    }

    fun displayAllEvent(){
        homeViewModel.getAllEvent().observe(viewLifecycleOwner, { eventList ->
            if (eventList.size > 0) {
                val comparator = Comparator { event1: Event, event2: Event -> event1.createdAt.compareTo(event2.createdAt)*-1 }
                eventAdapter.setEventData(eventList.sortedWith(comparator))
            }
        })
    }

    fun getCurrentUser(){
        homeViewModel.getUserInfo().observe(viewLifecycleOwner,{ currentUser ->
            if (currentUser != null){
                Picasso.get().load(currentUser.avatar_url).into(viewBinding.civAvatar)
                viewBinding.twUsername.text = currentUser.user_name
            }
        })
    }

    fun buildEventRecyclerView() {
        eventAdapter = EventAdapter(requireContext())
        rcv_event.adapter = eventAdapter
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.reverseLayout
        rcv_event.layoutManager = mLayoutManager

        eventAdapter.setItemActionListener(object : EventAdapter.ItemActionListener{
            override fun clickLikeOrDislikeListener(eventId: String, position:Int) {
                homeViewModel.handleLikeOrDislikeEvent(eventId)
                eventAdapter.notifyItemChanged(position)
            }

            override fun clickCommentsIcon(eventId: String) {
                val action = HomeFragmentDirections.actionHomeFragmentToCommentFragment(eventId)
                findNavController().navigate(action)
            }

            override fun clickEvent(eventId: String) {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailEventFragment(eventId)
                findNavController().navigate(action)
            }

            override fun clickQRCode(eventId: String) {
                val bundle = Bundle()
                bundle.putString("event_id",eventId)
                val qrCodeGeneratorDialog = QRCodeGeneratorFragment()
                activity?.supportFragmentManager?.let {
                    qrCodeGeneratorDialog.arguments = bundle
                    qrCodeGeneratorDialog.show(it, QRCodeGeneratorFragment::class.java.name)
                }
            }

            override fun clickAvatar(userId: String) {
                toUserProFile(userId)
            }

            override fun clickUserName(userId: String) {
                toUserProFile(userId)
            }

            override fun clickJoinEvent(eventId: String) {
                joinEvent(eventId)
            }

        })
    }

    private fun joinEvent(eventId:String){
        homeViewModel.joinEvent(eventId)
        homeViewModel.resultOfJoinEvent?.observe(viewLifecycleOwner,{
            Log.d("ResultJoinEvent", it.toString())
        })
    }

    private fun toUserProFile(userId: String){
        val currentUid  = homeViewModel.getCurrentUid()
        if (userId.equals(currentUid)){
            findNavController().navigate(R.id.profileFragment)
        }
        else{
            val action = HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(userId)
            findNavController().navigate(action)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            (R.id.civ_avatar) -> {
                findNavController().navigate(R.id.profileFragment)
            }

        }
    }


}