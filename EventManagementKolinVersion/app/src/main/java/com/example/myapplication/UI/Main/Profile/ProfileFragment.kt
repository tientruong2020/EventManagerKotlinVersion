package com.example.myapplication.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.ui.authentication.AuthenticationActivity
import com.example.myapplication.adapter.ViewPagerAdapter
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.model.User
import com.example.myapplication.viewModel.ProfileViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.lang.Exception


class ProfileFragment : Fragment(), View.OnClickListener {

    private  lateinit var profileViewModel: ProfileViewModel
    private lateinit var userLiveData: MutableLiveData<User>
    private lateinit var viewBinding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserProfileData()
        setAction()
        initViewPager()
    }

    fun setAction(){
        btn_to_change_profile.setOnClickListener(this)
        viewBinding.iwEmail.setOnClickListener(this)
        viewBinding.twEmail.setOnClickListener(this)
        viewBinding.btnLogout.setOnClickListener(this)
    }

    fun init(){
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

    }

    fun setUserProfileData(){
        userLiveData = profileViewModel.getUserProfile()
        userLiveData.observe(viewLifecycleOwner , { userInfo ->
            if (userInfo.avatar_url != ""){
                Picasso.get().load(userInfo.avatar_url).into(imv_avatar)
            }else{
                imv_avatar.setImageResource(R.drawable.defaultavatar)
            }
            tw_email.text = userInfo.email
            tw_username.text = userInfo.user_name
            tw_bio.text = userInfo.bio
        })
        getNumberOfEvents()
        getNumberOfFollower()
        getNumberOfFollowing()
    }

    override fun onClick(v: View?) {
        when(v){
            (viewBinding.btnToChangeProfile) -> {
                toEditProfile()
            }
            (viewBinding.iwEmail) ->{
                openEmail()
            }
            (viewBinding.twEmail) -> {
                openEmail()
            }
            (viewBinding.btnLogout) -> {
                logout()
            }
        }
    }

    private fun logout() {
        profileViewModel.signout()
        if (!profileViewModel.isLogined()){
            toAuthentication()
        }
    }
    fun toAuthentication(){
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun openEmail(){
        val recipient = viewBinding.twEmail.text
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient.toString()))

        try {
            startActivity(Intent.createChooser(intent,"Choose Email Client"))
        }
        catch (e:Exception){
            Log.d("open_email_exception",e.message.toString())
        }
    }

    private fun toEditProfile(){
        userLiveData.observe(viewLifecycleOwner,{userInfo ->
            val action = ProfileFragmentDirections.actionProfileFragmentToProfileEditorFragment(userInfo)
            findNavController().navigate(action)
        })
    }
    private fun getNumberOfEvents(){
        val currentUid = profileViewModel.getCurrentUid()
        profileViewModel.countEvents(currentUid).observe(viewLifecycleOwner,{
            viewBinding.twEventNumber.text = it.toString()
        })
    }

    private fun getNumberOfFollower(){
        val currentUid = profileViewModel.getCurrentUid()
        Log.d("currentUidFollower", currentUid)
        profileViewModel.countFollower(currentUid).observe(viewLifecycleOwner,{
            viewBinding.twFollowerNumber.text = it.toString()
        })
    }
    private fun getNumberOfFollowing(){
        val currentUid = profileViewModel.getCurrentUid()
        profileViewModel.countFollowing(currentUid).observe(viewLifecycleOwner,{
            viewBinding.twFollowingNumber.text = it.toString()
        })
    }

    private fun initViewPager(){
        val adapter = ViewPagerAdapter(this.parentFragmentManager)
        adapter.addFragment(MyEventFragment(),"My Event")
        adapter.addFragment(MyFriendFragment(), "Following User")
        viewBinding.viewPager.adapter = adapter
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager)
    }
}