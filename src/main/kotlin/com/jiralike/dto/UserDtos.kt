package com.jiralike.dto

import com.jiralike.domain.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// ========== User DTOs ==========

data class CreateUserRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,

    val role: Role = Role.USER
)

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val role: Role? = null,
    val isActive: Boolean? = null,
    val avatarUrl: String? = null,
    val notifyNewTask: Boolean? = null,
    val notifyComment: Boolean? = null,
    val notifyMention: Boolean? = null
)

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: Role,
    val isActive: Boolean,
    val avatarUrl: String?,
    val notifyNewTask: Boolean,
    val notifyComment: Boolean,
    val notifyMention: Boolean,
    val createdAt: LocalDateTime
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val newPassword: String
)
