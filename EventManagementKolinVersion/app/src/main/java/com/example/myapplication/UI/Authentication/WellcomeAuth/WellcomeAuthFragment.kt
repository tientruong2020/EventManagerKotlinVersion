package com.example.myapplication.ui.authentication.WellcomeAuth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_wellcome_auth.*


class WellcomeAuthFragment : Fragment(), View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_wellcome_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_tologin.setOnClickListener(this)
        btn_tosignup.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            (R.id.btn_tologin) ->{
                toLoginFragment()
            }
            (R.id.btn_tosignup) ->{
                toSignupFragment()
            }
        }
    }

    fun toLoginFragment(){
        findNavController().navigate(R.id.action_wellcomeAuthFragment_to_loginFragment)
    }

    fun toSignupFragment(){
        findNavController().navigate(R.id.action_wellcomeAuthFragment_to_registerFragment)
    }
}