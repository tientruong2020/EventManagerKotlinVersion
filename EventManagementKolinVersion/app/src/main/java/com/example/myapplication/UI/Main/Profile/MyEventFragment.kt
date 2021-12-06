package com.example.myapplication.UI.Main.Profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.MyEventAdapter
import com.example.myapplication.databinding.FragmentMyEventBinding
import com.example.myapplication.viewModel.ProfileViewModel


class MyEventFragment : Fragment() {
    lateinit var viewBinding:FragmentMyEventBinding
    lateinit var viewModel:ProfileViewModel
    lateinit var myEventAdapter: MyEventAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyEventBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildRecyclerView()
        getMyEvent()
    }

    fun buildRecyclerView(){
        myEventAdapter = MyEventAdapter()
        viewBinding.rcvMyEvent.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.rcvMyEvent.adapter = myEventAdapter
    }

    fun getMyEvent(){
        val currentUid = viewModel.getCurrentUid()
        Log.d("CurrentUid", currentUid)
        viewModel.getMyEvent(currentUid).observe(viewLifecycleOwner,{
            if(it.size >0){
                myEventAdapter.setAdapterData(it)
            }
            else{
                viewBinding.rcvMyEvent.visibility = View.GONE
                viewBinding.twNotifyNodata.visibility = View.VISIBLE
            }

        })
    }
}