package com.pdiot.harty.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.R
import com.pdiot.harty.utils.Validator

/* This Kotlin class allows a user to login into their account. */
class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var signUp: TextView
    private lateinit var forgotPassword: TextView
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setUpNavigation()

        loginButton = findViewById(R.id.loginButton)
        signUp = findViewById(R.id.signUpButton)
        forgotPassword = findViewById(R.id.forgotPasswordButton)
        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        auth = Firebase.auth

        loginButton.setOnClickListener {
            val emailString = email.text.toString().trim()
            val passwordString = password.text.toString().trim()

            //Completes input validation checks

            if (!Validator.validateEmail(emailString)) {
                Toast.makeText(baseContext, "Please provide a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Validator.validatePassword(passwordString)) {
                Toast.makeText(baseContext, "Please provide a password greater than 6 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Main process for signing in
            auth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    } else {
                        val taskException = task.exception.toString().substringAfter(": ")
                        Toast.makeText(baseContext, taskException, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            return@setOnClickListener
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
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