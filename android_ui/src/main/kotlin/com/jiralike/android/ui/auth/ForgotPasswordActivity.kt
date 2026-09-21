package com.jiralike.android.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jiralike.android.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToLoginText.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Call API to request password reset
            Toast.makeText(this, "Reset instructions sent to $email", Toast.LENGTH_SHORT).show()
        }
    }
}
