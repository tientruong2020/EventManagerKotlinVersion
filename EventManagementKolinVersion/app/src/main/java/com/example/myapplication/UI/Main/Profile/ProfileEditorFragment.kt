package com.example.myapplication.UI.Main.Profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.model.User
import com.example.myapplication.viewModel.ProfileEditorViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_editor.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileEditorFragment : Fragment(), View.OnClickListener {

    private lateinit var profileEditorViewModel: ProfileEditorViewModel
    private lateinit var currentUser: User
    private var localImgUri: Uri? = null
    private lateinit var currentPhotoPath:String

    companion object{
        const val REQUEST_OPEN_GALLERY_CODE = 100
        const val REQUEST_OPEN_CAMERA_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val arg = ProfileEditorFragmentArgs.fromBundle(it)
            currentUser = arg.currentUserInfo
            Log.d("CurrentUserEmail", currentUser.email)
        }
        profileEditorViewModel = ViewModelProvider(this).get(ProfileEditorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataForUi()
        btn_back_to_profile.setOnClickListener(this)
        btn_open_change_avatar_dialog.setOnClickListener(this)
        btn_save.setOnClickListener(this)
    }

    private fun setDataForUi() {
        et_username.text = Editable.Factory.getInstance().newEditable(currentUser.user_name)
        et_bio.text = Editable.Factory.getInstance().newEditable(currentUser.bio)
        if(currentUser.avatar_url != ""){
            Picasso.get().load(currentUser.avatar_url).into(imv_avatar)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            (R.id.btn_back_to_profile) -> {
                backToProfile()
            }
            (R.id.btn_open_change_avatar_dialog) -> {
                openPickImageOptionDialog()
            }
            (R.id.btn_save) -> {
                handleClickSaveButton()
            }
        }
    }

    private fun handleClickSaveButton(){
        localImgUri?.let {
            val updatedUserName = et_username.text.toString()
            val user = currentUser
            user.user_name = updatedUserName
            profileEditorViewModel.updateUserInfo(user, it).observe(viewLifecycleOwner, { result ->
                if(result){
                    backToProfile()
                }
            })
        }
        if (localImgUri == null){
            updateUserWithoutAvatar()
        }
    }



    private fun updateUserWithoutAvatar(){
        val updatedUserName = et_username.text.toString()
        val updatedBio = et_bio.text.toString()
        val user = currentUser
        user.user_name = updatedUserName
        user.bio = updatedBio
        profileEditorViewModel.updateUserWithoutAvatar(user).observe(viewLifecycleOwner,{ result ->
            if(result){
                backToProfile()
            }
        })
    }

    private fun backToProfile() {
        val action = ProfileEditorFragmentDirections.actionProfileEditorFragmentToProfileFragment()
        findNavController().navigate(action)
    }

    private fun openPickImageOptionDialog() {
        val pickImageOptionDialog = PickImageOptionFragment()
        activity?.supportFragmentManager?.let {
            pickImageOptionDialog.show(it, PickImageOptionFragment::class.java.name)
        }

        pickImageOptionDialog.setDialogChooseOptionListener(object :
            PickImageOptionFragment.onClickListener {

            override fun onClickOpenGallery() {
                openGallery()
                pickImageOptionDialog.dismiss()
            }

            override fun onClickOpenCamera() {
                openCamera()
                pickImageOptionDialog.dismiss()
            }

        })
    }

    private fun openCamera(){
        if (!isGrandedCameraPermisson()){
            requestPermissions(arrayOf(Manifest.permission.CAMERA),REQUEST_OPEN_CAMERA_CODE)
        }else{
            captureImageFromCamera()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile():File{
        val timestamp:String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir:File = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun captureImageFromCamera(){
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        resultLauncher.launch(intent)
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException){
                    Log.d("GetImageFromCamera", ex.toString())
                    null
                }

                photoFile?.also {
                    val photoUri:Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    localImgUri = photoUri
                    Log.d("AvatarUri", photoUri.toString())

                    resultLauncher.launch(takePictureIntent)
                }
            }
        }
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imv_avatar.setImageURI(localImgUri)
        }
    }

    private fun isGrandedCameraPermisson(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun openGallery() {
        if (!isGrandReadExternalStoragePermisson()){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_OPEN_GALLERY_CODE)
        }else{
            pickImgFromGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_OPEN_GALLERY_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                }
            }
            REQUEST_OPEN_CAMERA_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                }
            }
        }
    }

    fun pickImgFromGallery(){
        resultPickImageFromGallery.launch("image/*")
    }

    var resultPickImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()){imgUri ->
        imv_avatar.setImageURI(imgUri)
        Log.d("AvatarUri", imgUri.toString())
        localImgUri = imgUri
    }

    private fun isGrandReadExternalStoragePermisson(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }


}