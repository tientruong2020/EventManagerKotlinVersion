package com.example.myapplication.UI.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.SearchedUserAdapter
import com.example.myapplication.adapter.UserAdapter
import com.example.myapplication.databinding.FragmentSearchUserBinding
import com.example.myapplication.viewModel.SearchViewModel
import com.example.myapplication.viewModel.SharedViewModel


class SearchUserFragment : Fragment() {

    val sharedViewModel by activityViewModels<SharedViewModel>()
    lateinit var searchViewModel: SearchViewModel
    lateinit var searchedUserAdapter: SearchedUserAdapter
    lateinit var viewBinding: FragmentSearchUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentSearchUserBinding.inflate(inflater, container, false)
        buildAdapter()
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSearchText()
    }

    fun getSearchText(){
        sharedViewModel.getSearchingText().observe(requireActivity(),{
           val searchingText = it
            searchViewModel.searchUser(searchingText).observe(viewLifecycleOwner, {
                searchedUserAdapter.setDataForAdapter(it)
            })
        })
    }

    fun buildAdapter(){
        searchedUserAdapter = SearchedUserAdapter(requireContext())
        viewBinding.rcvSearchedUser.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        viewBinding.rcvSearchedUser.adapter = searchedUserAdapter
        searchedUserAdapter.setItemActionListener(object : SearchedUserAdapter.ItemActionListener{
            override fun clickItem(userId: String) {
                val action = SearchFragmentDirections.actionSearchFragmentToOtherProfileFragment(userId)
                findNavController().navigate(action)
            }

        })
    }
}