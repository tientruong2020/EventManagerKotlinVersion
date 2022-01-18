package com.example.myapplication.ui.search

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.SearchingHistoryAdapter
import com.example.myapplication.adapter.ViewPagerAdapter
import com.example.myapplication.databinding.FragmentSearchBinding
import com.example.myapplication.model.HistoricalSearchingText
import com.example.myapplication.viewModel.SearchViewModel
import com.example.myapplication.viewModel.SharedViewModel


class SearchFragment : Fragment(), View.OnClickListener{
    private lateinit var viewBinding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var searchingHistoryAdapter: SearchingHistoryAdapter
    companion object{
        const val REQUEST_OPEN_CAMERA_CODE_CODE = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAction()
        buildRecyclerView()
        setDataForAdapter()
        setUpTabs()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAction(){
        viewBinding.ivOpenScanner.setOnClickListener(this)
        viewBinding.searchView.setIconifiedByDefault(true)
        viewBinding.searchView.focusable = View.FOCUSABLE

        viewBinding.searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!TextUtils.isEmpty(query)){
                    val createdAt = System.currentTimeMillis()
                    val historicalSearchingText = HistoricalSearchingText(0,query!!,createdAt)
                    searchViewModel.addSearchingHistory(historicalSearchingText)
                    sharedViewModel.setSearchingText(query)
                    viewBinding.rcvSearchingHistory.visibility = View.GONE
                    viewBinding.resultSearchLayout.visibility = View.VISIBLE

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (TextUtils.isEmpty(newText)){
                    viewBinding.rcvSearchingHistory.visibility = View.VISIBLE
                    viewBinding.resultSearchLayout.visibility = View.GONE
                }
                return false
            }

        })
    }
    fun setUpTabs(){
        val adapter = ViewPagerAdapter(this.parentFragmentManager)
        adapter.addFragment(SearchEventFragment(),"Event")
        adapter.addFragment(SearchUserFragment(),"User")
        viewBinding.viewPager.adapter = adapter
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager)

    }


    fun buildRecyclerView(){
        searchingHistoryAdapter = SearchingHistoryAdapter(this.requireContext())
        viewBinding.rcvSearchingHistory.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,false)
        viewBinding.rcvSearchingHistory.adapter = searchingHistoryAdapter
        searchingHistoryAdapter.setItemListener(object : SearchingHistoryAdapter.ItemListener{
            override fun deleteHistory(history: HistoricalSearchingText) {
                searchViewModel.deleteHistory(history)
                setDataForAdapter()
            }

            override fun clickHistory(history: HistoricalSearchingText) {
                viewBinding.searchView.setQuery(history.content,true)
            }

        })
    }
    fun setDataForAdapter(){
        searchViewModel.allSearchingHistory?.observe(viewLifecycleOwner,{
            if (it.isNotEmpty()) {
                searchingHistoryAdapter.setAdapterData(it)
            }
        })
    }

    override fun onClick(v: View?) {
        when(v){
            (viewBinding.ivOpenScanner) -> {
                if(!isGrandCameraPerMission()){
                    requestCameraPermission()
                }
                else{
                    openScanner()
                }
            }
        }
    }

    fun openScanner(){
        val action = SearchFragmentDirections.actionSearchFragmentToQRCodeScannerFragment()
        findNavController().navigate(action)
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA),
            REQUEST_OPEN_CAMERA_CODE_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_OPEN_CAMERA_CODE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openScanner()
                }
            }
        }
    }

    fun isGrandCameraPerMission():Boolean{
            return (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED)
    }
}