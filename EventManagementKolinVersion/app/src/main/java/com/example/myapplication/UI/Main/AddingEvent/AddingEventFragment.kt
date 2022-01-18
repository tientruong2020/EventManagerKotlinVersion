package com.example.myapplication.ui.main.addingevent

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.PickedImagesAdapter
import com.example.myapplication.databinding.FragmentAddingEventBinding
import com.example.myapplication.model.Event
import com.example.myapplication.viewModel.AdditionEventViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddingEventFragment : Fragment(), View.OnClickListener {
    private lateinit var viewBinding: FragmentAddingEventBinding
    var imageUriList: ArrayList<Uri> = ArrayList<Uri>()
    private lateinit var pickedImagesAdapter: PickedImagesAdapter
    private lateinit var additionEventViewModel: AdditionEventViewModel
    var isOnlineEvent = false


    companion object {
        const val ERROR_MESSAGE = "This field is empty!!"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        additionEventViewModel = ViewModelProvider(this).get(AdditionEventViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentAddingEventBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildPickedImagesRecyclerView()
        setAction()
    }

    fun setAction() {
        viewBinding.btnEndDatePicker.setOnClickListener(this)
        viewBinding.btnStartDatePicker.setOnClickListener(this)
        viewBinding.btnOpenGallery.setOnClickListener(this)
        handleSwitchUnlimit()
        handleOnlineRadioBtnCheck()
        viewBinding.btnSaveEvent.setOnClickListener(this)
    }

    fun handleSwitchUnlimit() {
        viewBinding.switchUnlimit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewBinding.etMemberNumber.text =
                    Editable.Factory.getInstance().newEditable("0")
                viewBinding.etMemberNumberLayout.visibility = View.GONE
            } else {
                viewBinding.etMemberNumberLayout.visibility = View.VISIBLE
                viewBinding.etMemberNumber.text =
                    Editable.Factory.getInstance().newEditable("1")

            }
        }
    }

    private fun handleOnlineRadioBtnCheck() {
        viewBinding.radioGroup.setOnCheckedChangeListener(object :
            RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    (R.id.offlineRB) -> {
                        viewBinding.placeOrLinkLayout.hint = "Place"
                        isOnlineEvent = false
                    }
                    (R.id.onlineRB) -> {
                        viewBinding.placeOrLinkLayout.hint = "Event Link"
                        isOnlineEvent = true
                    }
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            (R.id.btn_end_date_picker) -> {
                showDateTimeDialog(viewBinding.etEndDate)
            }
            (R.id.btn_start_date_picker) -> {
                showDateTimeDialog(viewBinding.etStartDate)
            }
            (R.id.btn_open_gallery) -> {
                openGallery()
            }
            (R.id.btn_save_event) -> {
                saveEvent()
            }
        }
    }

    private fun showDateTimeDialog(editText: EditText) {
        val calendar: Calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext())
        datePickerDialog.setOnDateSetListener(object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                calendar.set(year, month, dayOfMonth)
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
                        editText.text = Editable.Factory.getInstance()
                            .newEditable(simpleDateFormat.format(calendar.time))
                    }
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
        })
        datePickerDialog.show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        mutipleImagesPicker.launch(intent)
    }

    var mutipleImagesPicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data?.clipData != null) {
                val imagesPickedNumber = result.data?.clipData!!.itemCount
                for (i in 0..imagesPickedNumber - 1) {
                    imageUriList.add(result.data?.clipData!!.getItemAt(i).uri)
                    pickedImagesAdapter.setData(imageUriList)
                }
            } else if (result.data != null) {
                result.data?.data?.let {
                    imageUriList.add(it)
                    pickedImagesAdapter.setData(imageUriList)
                }
            }
        }

    private fun buildPickedImagesRecyclerView() {
        pickedImagesAdapter = PickedImagesAdapter(requireContext())
        viewBinding.rcvPickedImages.adapter = pickedImagesAdapter
        viewBinding.rcvPickedImages.layoutManager =
            GridLayoutManager(activity, 3, LinearLayoutManager.VERTICAL, false)

        pickedImagesAdapter.setClickListener(object : PickedImagesAdapter.ClickListener{
            override fun clickRemove(position: Int) {
                imageUriList.removeAt(position)
                pickedImagesAdapter.setData(imageUriList)
            }

        })
    }

    private fun saveEvent() {
        val eventName: String = viewBinding.etEventName.text.toString()
        val placeOrLink: String = viewBinding.etPlaceOrLink.text.toString()
        val description = viewBinding.etDescription.text.toString()
        val startDate: String = viewBinding.etStartDate.text.toString()
        val endDate: String = viewBinding.etEndDate.text.toString()
        val limit: String = viewBinding.etMemberNumber.text.toString()
        if (AddtionalEventValidator.isEmptyField(eventName)) {
            viewBinding.etEventName.error = ERROR_MESSAGE
        }
        if (AddtionalEventValidator.isEmptyField(placeOrLink)){
            viewBinding.etPlaceOrLink.error = ERROR_MESSAGE
        }
        if (AddtionalEventValidator.isEmptyField(description)){
            viewBinding.etDescription.error = ERROR_MESSAGE
        }
        if (AddtionalEventValidator.isEmptyField(description)){
            viewBinding.etDescription.error = ERROR_MESSAGE
        }
        if (AddtionalEventValidator.isEmptyField(startDate)){
            viewBinding.etStartDate.error = ERROR_MESSAGE
        }
        if (AddtionalEventValidator.isEmptyField(endDate)){
            viewBinding.etEndDate.error = ERROR_MESSAGE
        }
        if (AddtionalEventValidator.isEmptyField(limit)){
            viewBinding.etMemberNumber.error = ERROR_MESSAGE
        }
        if (AddtionalEventValidator.isNotEmptyAllFields(eventName, placeOrLink, description, startDate, endDate, limit)){
            if(isOnlineEvent){
                if (AddtionalEventValidator.validateWebUrl(placeOrLink)){
                    if (AddtionalEventValidator.checkEndDate(startDate, endDate)){
                        if(imageUriList.size > 0){
                            additionEventViewModel.addImgEvents(imageUriList).observe(viewLifecycleOwner,{
                                val uid = additionEventViewModel.getUid()
                                val createdAt = System.currentTimeMillis()
                                val event = Event(eventName,description,placeOrLink,uid,endDate, startDate, isOnlineEvent, limit.toLong(),createdAt, it)
                                additionEventViewModel.addEventToDB(event).observe(viewLifecycleOwner,{
                                    if (it){
                                        findNavController().navigate(R.id.homeFragment)
                                    }
                                    else{
                                        Log.d("Add Event", "is faile")
                                    }
                                })
                            })
                        }
                        else{
                            Toast.makeText(requireContext(), "Please!! pick images for your event", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        viewBinding.etEndDate.error = "End date is before Start date"
                    }
                }
                else{
                    viewBinding.etPlaceOrLink.error = "You entered WebUrl which is not Url Pattern"
                }
            }
            else{
                if (AddtionalEventValidator.checkEndDate(startDate, endDate)){
                    if(imageUriList.size > 0){
                        additionEventViewModel.addImgEvents(imageUriList).observe(viewLifecycleOwner,{
                            val uid = additionEventViewModel.getUid()
                            val createdAt = System.currentTimeMillis()
                            val event = Event(eventName,description,placeOrLink,uid,endDate, startDate, isOnlineEvent, limit.toLong(),createdAt, it)
                            additionEventViewModel.addEventToDB(event).observe(viewLifecycleOwner,{
                                if (it){
                                    Log.d("Add Event", "is success")
                                    viewBinding.etEventName.text?.clear()
                                }
                                else{
                                    Log.d("Add Event", "is faile")
                                }
                            })
                        })
                    }
                    else{
                        Toast.makeText(requireContext(), "Please!! pick images for your event", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    viewBinding.etEndDate.error = "End date is before Start date"
                }
            }
        }
    }



}