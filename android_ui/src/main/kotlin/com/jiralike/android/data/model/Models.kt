package com.jiralike.android.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val email: String,
    val name: String,
    val role: String
)

data class ProjectDto(
    val id: Long,
    val name: String,
    val key: String?,
    val description: String?,
    val ownerEmail: String?
)

data class CreateProjectRequest(
    val name: String,
    val key: String,
    val description: String
)

data class IssueDto(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String, // TODO, IN_PROGRESS, IN_REVIEW, DONE
    val type: String,   // BUG, FEATURE, TASK, STORY, EPIC
    val priority: String, // LOWEST, LOW, MEDIUM, HIGH, HIGHEST
    val assignee: UserDto?,
    val dueDate: String?
)

data class CreateIssueRequest(
    val title: String,
    val description: String,
    val type: String = "TASK",
    val priority: String = "MEDIUM"
)

data class ProjectDetailDto(
    val id: Long,
    val name: String,
    val description: String?,
    val members: List<UserDto>
)

data class UpdateStatusRequest(
    val status: String
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String
)
