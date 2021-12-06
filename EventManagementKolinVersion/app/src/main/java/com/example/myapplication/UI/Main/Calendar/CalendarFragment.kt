package com.example.myapplication.UI.Main.Calendar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.MyEventAdapter
import com.example.myapplication.databinding.FragmentCalendarBinding
import com.example.myapplication.model.Event
import com.example.myapplication.viewModel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarFragment : Fragment() {

    private lateinit var viewBinding: FragmentCalendarBinding
    private lateinit var eventAdapter: MyEventAdapter
    private lateinit var viewModel : CalendarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventAdapter = MyEventAdapter()
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCalendarBinding.inflate(inflater, container, false)
        handlePickDate()
        buildRecyclerView()
        return viewBinding.root
    }

    fun handlePickDate(){
        viewBinding.calendarView.setOnDateChangeListener(object : CalendarView.OnDateChangeListener{
            val calendar = Calendar.getInstance()
            override fun onSelectedDayChange(
                view: CalendarView,
                year: Int,
                month: Int,
                dayOfMonth: Int
            ) {
                val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
                calendar.set(year, month, dayOfMonth)
                eventAdapter.setAdapterData(ArrayList<Event>())
                viewModel.getEventsByDate(simpleDateFormat.format(calendar.time)).observe(viewLifecycleOwner,{mEvents ->
                    eventAdapter.setAdapterData(mEvents)
                })
                viewModel.getJoinedEventsBYDate(simpleDateFormat.format(calendar.time)).observe(viewLifecycleOwner,{
                    eventAdapter.addEvents(it)
                })
            }

        })
    }

    fun buildRecyclerView(){
        eventAdapter = MyEventAdapter()
        viewBinding.EventRCV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.EventRCV.adapter = eventAdapter
    }


}