package com.jiralike.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// ========== Project DTOs ==========

data class CreateProjectRequest(
    @field:NotBlank(message = "Project name is required")
    val name: String,

    @field:NotBlank(message = "Project key is required")
    @field:Pattern(regexp = "^[A-Z]{2,10}$", message = "Key must be 2-10 uppercase letters")
    val key: String,

    val description: String? = null
)

data class UpdateProjectRequest(
    val name: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null
)

data class AddMemberRequest(
    val userId: Long
)

data class ProjectSummaryResponse(
    val id: Long,
    val name: String,
    val key: String,
    val description: String?,
    val ownerName: String,
    val memberCount: Int,
    val issueCount: Int,
    val isActive: Boolean,
    val createdAt: LocalDateTime
)

data class ProjectDetailResponse(
    val id: Long,
    val name: String,
    val key: String,
    val description: String?,
    val owner: UserResponse,
    val members: List<UserResponse>,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
