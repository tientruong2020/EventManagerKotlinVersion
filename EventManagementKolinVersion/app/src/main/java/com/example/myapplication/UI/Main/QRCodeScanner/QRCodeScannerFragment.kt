package com.example.myapplication.ui.main.qrcodescanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.myapplication.databinding.FragmentQRCodeScannerBinding
import com.example.myapplication.viewModel.ScannerViewModel

class QRCodeScannerFragment : Fragment() {
    lateinit var viewBinding: FragmentQRCodeScannerBinding
    lateinit var codeScanner : CodeScanner
    lateinit var scannerViewModel: ScannerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerViewModel = ViewModelProvider(this).get(ScannerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentQRCodeScannerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity =  requireActivity()
        codeScanner = CodeScanner(activity,viewBinding.qrScannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val eventId = it.text
                if (ScannerValidator.isFireBasePath(eventId)){
                    scannerViewModel.isExitEvent(eventId).observe(viewLifecycleOwner, { result ->
                        if(result){
                            val action = QRCodeScannerFragmentDirections.actionQRCodeScannerFragmentToDetailEventFragment(eventId)
                            findNavController().navigate(action)
                        }
                        else{
                            codeScanner.startPreview()
                            Toast.makeText(requireContext(), "This QRCode is invalid!!", Toast.LENGTH_SHORT).show()
                        }
                    })
                }else{
                    codeScanner.startPreview()
                    Toast.makeText(requireContext(), "This QRCode is invalid!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewBinding.qrScannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }

}