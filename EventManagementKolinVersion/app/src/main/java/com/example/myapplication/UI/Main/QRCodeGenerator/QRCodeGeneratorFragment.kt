package com.example.myapplication.ui.main.qrcodegenerator

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myapplication.databinding.FragmentQRCodeGeneratorBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder


class QRCodeGeneratorFragment : DialogFragment(), View.OnClickListener {

    private lateinit var eventId:String
    private lateinit var viewBinding: FragmentQRCodeGeneratorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventId = it.getString("event_id").toString()
            Log.d("eventid", eventId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentQRCodeGeneratorBinding.inflate(inflater,container,false)
        return viewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateQRCode()
        setAction()
    }

    fun setAction(){
        viewBinding.btnCancel.setOnClickListener(this)
    }

    fun generateQRCode(){
        val qrcodeWriter = QRCodeWriter()
        try {
            val bitMatric = qrcodeWriter.encode(eventId, BarcodeFormat.QR_CODE, 512,512)
            val encoder = BarcodeEncoder()
            val bitmap:Bitmap = encoder.createBitmap(bitMatric)
            viewBinding.ivQrcode.setImageBitmap(bitmap)

        }catch (e:WriterException){
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when(v){
            (viewBinding.btnCancel) ->{
                dismiss()
            }
        }
    }
}