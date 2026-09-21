package com.jiralike.dto

import com.jiralike.domain.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// ========== Auth DTOs ==========

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class TokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val userId: Long,
    val name: String,
    val email: String,
    val role: Role
)

data class ForgotPasswordRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String
)

data class ForgotPasswordResponse(
    val message: String,
    val resetToken: String,  // In a real app this would only be sent via email
    val expiresIn: String = "1 hour"
)

data class ResetPasswordRequest(
    @field:NotBlank(message = "Token is required")
    val token: String,

    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val newPassword: String,

    @field:NotBlank(message = "Confirm password is required")
    val confirmPassword: String
)
