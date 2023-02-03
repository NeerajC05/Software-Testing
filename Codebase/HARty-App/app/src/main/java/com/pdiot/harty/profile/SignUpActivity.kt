package com.pdiot.harty.profile

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.R
import com.pdiot.harty.utils.Validator

/* This Kotlin class allows a user to create an account for the application. */
class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpButton: Button
    private lateinit var loginButton: TextView
    private lateinit var name : EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setUpNavigation()

        signUpButton = findViewById(R.id.signUpButton)
        loginButton = findViewById(R.id.loginButton)
        name = findViewById(R.id.name)
        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        auth = Firebase.auth

        signUpButton.setOnClickListener {
            val nameString = name.text.toString().trim()
            val emailString = email.text.toString().trim()
            val passwordString = password.text.toString().trim()

            //Completes input validation checks
            if (nameString.isEmpty()) {
                Toast.makeText(baseContext, "Please provide a full name.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Validator.validateEmail(emailString)) {
                Toast.makeText(baseContext, "Please provide a valid email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Validator.validatePassword(passwordString)) {
                Toast.makeText(baseContext, "Please provide a password greater than 6 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Main process for creating an account
            auth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameString).build()
                        user!!.updateProfile(profileUpdates)
                        auth.signOut()
                        Toast.makeText(baseContext, "Sign up complete, please login.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val taskException = task.exception.toString().substringAfter(": ")
                        Toast.makeText(baseContext, taskException, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginButton.setOnClickListener {
            finish()
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