package com.example.teamup.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.teamup.R
import com.example.teamup.firebase.FireStoreClass
import com.example.teamup.models.User
import com.example.teamup.util.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUri: String = ""
    private lateinit var mUserDetail: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setUpActionBar()

        FireStoreClass().loadUserData(this@MyProfileActivity)

        iv_profile_user_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
            else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_update.setOnClickListener {
            if(mSelectedImageFileUri != null) {
                uploadUserImage()
            }
            else {
                showProgressDialog("Please Wait")
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
            else {
                Toast.makeText(this, "Permissions Denied. Please Allow It From Settings",
                Toast.LENGTH_LONG).show()
            }
        }
    }

    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK &&
            requestCode== Constants.PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null) {
            mSelectedImageFileUri = data.data!!

            try {
                Glide
                    .with(this)
                    .load(Uri.parse(mSelectedImageFileUri.toString()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_profile_user_image)
            }catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_my_profile_activity)
        val actionBar = supportActionBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "My Profile"
        }

        toolbar_my_profile_activity.setNavigationOnClickListener{onBackPressed()}
    }

    fun setUserDataInUI(user: User?) {
        if (user != null) {
            mUserDetail = user
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_profile_user_image)

            et_name.setText(user.name)
            et_email.setText(user.email)
            if (user.mobile != "0") {
                et_mobile.setText((user.mobile.toString()))
            }
        }
    }
    private fun updateUserProfileData() {
        var userHashMap = HashMap<String, Any>()

        if(mProfileImageUri.isNotEmpty() && mProfileImageUri != mUserDetail.image) {
            userHashMap[Constants.IMAGE] = mProfileImageUri
        }

        if(et_name.text.toString() != mUserDetail.name) {
            userHashMap[Constants.NAME] = et_name.text.toString()
        }

        if(et_mobile.text.toString() != mUserDetail.mobile.toString()) {
            userHashMap[Constants.MOBILE] = et_mobile.text.toString()
        }

        FireStoreClass().updateUserProfileData(this@MyProfileActivity, userHashMap)
    }

    private fun uploadUserImage() {
        showProgressDialog("Please Wait")
        if(mSelectedImageFileUri != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." +
                        Constants.getFileExtension(this, mSelectedImageFileUri))

            storageRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                    Log.i(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                        Log.e("Downloadable Image URI", uri.toString())
                        mProfileImageUri = uri.toString()

                        updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                    Toast.makeText(
                        this@MyProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                hideProgressDialog()
            }
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}