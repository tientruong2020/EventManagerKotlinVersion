package com.example.myapplication.UI.Main.ChangePasswordDialog

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.UI.Authentication.AuthenticationActivity
import com.example.myapplication.viewModel.ChangePasswordViewModel
import kotlinx.android.synthetic.main.fragment_change_password_dialog.*

class ChangePasswordDialog : DialogFragment(), View.OnClickListener {
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_cancel_change_pw.setOnClickListener(this)
        btn_confirm_change_pw.setOnClickListener(this)
    }

    private fun init(){
        changePasswordViewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            (R.id.btn_cancel_change_pw) -> {
                handleClickCancelButton()
            }
            (R.id.btn_confirm_change_pw) -> {
                handleClickConfirmButton()
            }
        }
    }

    private fun handleClickCancelButton(){
        dismiss()
    }

    private fun handleClickConfirmButton(){
        val currentPw = et_current_pw.text.toString()
        val newPw = et_new_pw.text.toString()
        val confirmNewPw = et_confirm_pw.text.toString()
        if (!ChangePasswordValidator.isFieldsEmpty(currentPw)){
            et_current_pw.error = "Please! Enter a password which has more than 6 character"
        }
        else if(!ChangePasswordValidator.isFieldsEmpty(newPw)){
            et_new_pw.error = "Please! Enter a password which has more than 6 character"
        }
        else{
            if (!ChangePasswordValidator.checkRepeatPassword(newPw,confirmNewPw)){
                et_confirm_pw.error = "Please! Repeat the confirm password which is like password"
            }else{
                changePasswordViewModel.changePassword(currentPw, newPw).observe(viewLifecycleOwner,{ result ->
                    if (result){
                        dismiss()
                        changePasswordViewModel.signout()
                        toAuthActivtyAfterChangedPw()
                    }
                    if (!result){
                        Toast.makeText(requireContext(), "Please!! Enter your correct Password", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
    private fun toAuthActivtyAfterChangedPw(){
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }


}