package com.example.myapplication.UI.Main.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.UserAdapter
import com.example.myapplication.databinding.FragmentMyFriendBinding
import com.example.myapplication.viewModel.ProfileViewModel


class MyFriendFragment : Fragment() {
    lateinit var profileViewModel: ProfileViewModel
    lateinit var viewBinding: FragmentMyFriendBinding
    lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMyFriendBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildRecyclerView()
        getFollowingUser()
    }

    fun buildRecyclerView(){
        userAdapter = UserAdapter()
        viewBinding.rcvMyFriend.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        viewBinding.rcvMyFriend.adapter = userAdapter
        userAdapter.setItemListener(object : UserAdapter.ItemActionListener{
            override fun clickUser(uid: String) {
                val action = ProfileFragmentDirections.actionProfileFragmentToOtherProfileFragment(uid)
                findNavController().navigate(action)
            }

        })
    }

    fun getFollowingUser(){
        profileViewModel.getFollowingUser().observe(viewLifecycleOwner, {
            userAdapter.setAdapterData(it)
        })
    }
}