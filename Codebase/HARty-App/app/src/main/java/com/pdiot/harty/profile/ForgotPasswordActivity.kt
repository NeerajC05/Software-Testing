package com.pdiot.harty.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.R
import com.pdiot.harty.utils.Validator

/* This Kotlin class allows a user to reset the password of their account, from the login page of the application. */
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var forgotPasswordButton : Button
    private lateinit var emailAddress : EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        setUpNavigation()

        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)
        emailAddress = findViewById(R.id.emailInput)
        auth = Firebase.auth

        forgotPasswordButton.setOnClickListener {
            val emailString = emailAddress.text.toString().trim()

            //Completes input validation checks
            if (!Validator.validateEmail(emailString)) {
                Toast.makeText(baseContext, "Please provide a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Main process for resetting the password of the account
            auth.sendPasswordResetEmail(emailString)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Email sent. Please check your spam folder.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        val taskException = task.exception.toString().substringAfter(": ")
                        Toast.makeText(baseContext, taskException, Toast.LENGTH_SHORT).show()
                    }
                }

            return@setOnClickListener
        }
    }

    //Sets the required navigation for the page
    private fun setUpNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.backNavigationView)

        bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.back -> {
                    finish()
                }
            }
            true
        }
    }
}