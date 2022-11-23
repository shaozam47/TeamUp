package com.example.teamup.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.teamup.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_sign_in.setOnClickListener {
            signInRegisteredUser()
        }
        setUpActionBar()
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_sign_in_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener{ onBackPressed() }
    }

    private fun signInRegisteredUser() {
        val email: String = et_email_sign_in.text.toString().trim{it <= ' '}
        val password: String = et_password_sign_in.text.toString().trim(){it <= ' '}

        if(validateForm(email, password)) {
            showProgressDialog("Please Wait")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Sign In", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign In", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, task.exception!!.message,
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password:String): Boolean {
        return when {
            TextUtils.isEmpty(email)->{
                showError("Please Enter A Email")
                false
            }
            TextUtils.isEmpty(password)-> {
                showError("Please Enter A Password")
                false
            }
            else -> {
                true
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this@SignInActivity, message, Toast.LENGTH_LONG).show()
    }
}