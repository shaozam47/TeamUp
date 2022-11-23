package com.example.teamup.activities


import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.teamup.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setUpActionBar()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun setUpActionBar() {
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar_sign_up_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener{onBackPressed()}

        btn_sign_up.setOnClickListener {
            registerUser() }
    }

    private fun registerUser() {
        val name: String = et_name.text.toString().trim{it <= ' '}
        val email: String = et_email.text.toString().trim{it <= ' '}
        val password: String = et_password.text.toString().trim(){it <= ' '}

        if(validateForm(name, email, password)) {
            Toast.makeText(this@SignUpActivity,
                "Now We Can Register A User",
            Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateForm(name: String, email: String, password:String): Boolean {
        return when {
            TextUtils.isEmpty(name)->{
                showError("Please Enter A Name")
                false
            }
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
        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_LONG).show()
    }
}