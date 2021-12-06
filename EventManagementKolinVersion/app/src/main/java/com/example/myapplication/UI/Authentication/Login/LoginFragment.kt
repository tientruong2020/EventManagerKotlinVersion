package com.example.myapplication.UI.Authentication.Login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.UI.Main.MainActivity
import com.example.myapplication.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment(), View.OnClickListener {

    lateinit var mLoginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_toregister.setOnClickListener(this)
        btn_dologin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            (R.id.btn_toregister) -> {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            (R.id.btn_dologin) -> {
                login()
            }
        }
    }

    fun login(){
        val email = et_email.text.toString()
        val password = et_password.text.toString()
        mLoginViewModel.loginWithEmail(email, password).observe(viewLifecycleOwner, Observer { result ->
            if (result){
                goToHomeActivity()
            }
        })
    }
    fun goToHomeActivity(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

}