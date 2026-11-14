package com.jiralike.android.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jiralike.android.data.local.SessionManager
import com.jiralike.android.data.model.LoginRequest
import com.jiralike.android.data.network.FirestoreRepository
import com.jiralike.android.databinding.ActivityLoginBinding
import com.jiralike.android.ui.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (SessionManager.getToken(this) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding.emailInput.setText("admin@jiralike.com")
        binding.passwordInput.setText("Admin@123")

        binding.forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val repo = FirestoreRepository()
                    val result = repo.login(LoginRequest(email, password))
                    
                    if (result.isSuccess) {
                        val authResponse = result.getOrNull()!!
                        // saving ID as token
                        SessionManager.saveToken(this@LoginActivity, authResponse.accessToken)

                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.registerButton.setOnClickListener {
            val name = binding.registerNameInput.text.toString().trim()
            val email = binding.registerEmailInput.text.toString().trim()
            val password = binding.registerPasswordInput.text.toString()
            val confirm = binding.registerConfirmPasswordInput.text.toString()
            val terms = binding.termsCheckbox.isChecked

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirm) {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!terms) {
                Toast.makeText(this, "Você precisa aceitar os termos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val repo = FirestoreRepository()
                    val req = com.jiralike.android.data.model.RegisterRequest(name, email, password)
                    val result = repo.register(req)
                    
                    if (result.isSuccess) {
                        Toast.makeText(this@LoginActivity, "Conta criada! Faça login.", Toast.LENGTH_SHORT).show()
                        binding.emailInput.setText(email)
                        binding.passwordInput.setText("")
                        binding.registerNameInput.setText("")
                        binding.registerEmailInput.setText("")
                        binding.registerPasswordInput.setText("")
                        binding.registerConfirmPasswordInput.setText("")
                    } else {
                        Toast.makeText(this@LoginActivity, "Erro ao criar conta: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Erro: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
