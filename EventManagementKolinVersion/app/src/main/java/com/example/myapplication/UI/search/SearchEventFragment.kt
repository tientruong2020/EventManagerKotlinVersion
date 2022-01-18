package com.example.myapplication.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.MyEventAdapter
import com.example.myapplication.databinding.FragmentSearchEventBinding
import com.example.myapplication.viewModel.SearchViewModel
import com.example.myapplication.viewModel.SharedViewModel


class SearchEventFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var viewBinding: FragmentSearchEventBinding
    private lateinit var myEventAdapter: MyEventAdapter
    val sharedViewModel by activityViewModels<SharedViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentSearchEventBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildRecyclerView()
        getSearchText()
    }

    fun getSearchText(){
        sharedViewModel.getSearchingText().observe(requireActivity(),{
            searchViewModel.searchEvent(it).observe(viewLifecycleOwner,{eventList ->
                myEventAdapter.setAdapterData(eventList)
            })
        })
    }

    fun buildRecyclerView(){
        myEventAdapter = MyEventAdapter()
        viewBinding.rcvSeachedEvent.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.rcvSeachedEvent.adapter = myEventAdapter
        myEventAdapter.setItemActionListener(object : MyEventAdapter.ItemActionListener{
            override fun clickItemListener(eventId: String) {
                val action = SearchFragmentDirections.actionSearchFragmentToDetailEventFragment(eventId)
                findNavController().navigate(action)
            }

        })

    }



}