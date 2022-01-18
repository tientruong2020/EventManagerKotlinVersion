package com.example.myapplication.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.myapplication.R
import com.example.myapplication.ui.authentication.AuthenticationActivity
import com.example.myapplication.ui.main.changepassworddialog.ChangePasswordDialog
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viewModel.MainViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(){
    private lateinit var mainViewModel: MainViewModel
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nav_view: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        init()
        isLogined()
        setContentView(binding.root)
        setBottomNavigation()
    }

    fun init(){
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        database = Firebase.database.getReference()
    }


    //set toggle button open and close left navigation
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun isLogined(){
        val currentUser = mainViewModel.isLogined()
        if(!currentUser){
            toAuthentication()
        }
    }

    private fun openChangePasswordDialog(){
        val changePasswordDialog = ChangePasswordDialog()
        supportFragmentManager?.let {
            changePasswordDialog.show(it, ChangePasswordDialog::class.java.name)
        }
    }

    fun toAuthentication(){
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    fun setBottomNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)
    }


}