package com.example.myapplication.UI.Authentication.Register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.viewModel.RegisterViewModel
import com.example.myapplication.R
import com.example.myapplication.UI.Main.MainActivity
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), View.OnClickListener {

    lateinit var mRegisterViewModel: RegisterViewModel
    lateinit var userRef: DatabaseReference
    val registerValidator = RegisterValidator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRegisterViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_doregister.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            (R.id.btn_doregister) -> {
                val userName = et_username.text.toString()
                val email = et_email.text.toString()
                val password = et_password.text.toString()
                val confirmPassword = et_confirm_password.text.toString()
                register(userName, email, password, confirmPassword)
            }
            (R.id.btn_cancel) -> {
                toWellcomeAuth()
            }
        }
    }

    fun register(userName: String, email: String, password: String, confirmPassword: String) {
        if (registerValidator.isEmail(email) && !registerValidator.emailIsEmpty(email) && registerValidator.checkLeghtPassword(
                password
            ) && registerValidator.checkRepeatPassword(password, confirmPassword)
        ) {
            with(mRegisterViewModel) {
                doRegister(userName, email, password).observe(viewLifecycleOwner,
                    Observer { result ->
                        if (result) {
                            toMainActivity()
                        }
                    })
            }

        }
        if (!registerValidator.isEmail(email)) {
            et_email.error = "Please! Enter a Email"
        }
        if (registerValidator.emailIsEmpty(email)) {
            et_email.error = "Email field is empty"
        }
        if (!registerValidator.checkLeghtPassword(password)) {
            et_password.error = "Please! Enter a password which has more than 6 character"
        }
        if (!registerValidator.checkRepeatPassword(password, confirmPassword)) {
            et_confirm_password.error =
                " Please! Repeat the confirm password which is like password"
        }

    }

    fun toMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    fun toWellcomeAuth() {
        findNavController().navigate(R.id.action_registerFragment_to_wellcomeAuthFragment)
    }

}