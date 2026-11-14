package com.jiralike.android.data.model

import com.google.firebase.firestore.DocumentId

data class LoginRequest(
    val email: String = "",
    val password: String = ""
)

data class RegisterRequest(
    val name: String = "",
    val email: String = "",
    val password: String = ""
)

data class AuthResponse(
    val accessToken: String = "", // We can use User ID here
    val tokenType: String = "Bearer",
    val email: String = "",
    val name: String = "",
    val role: String = "USER"
)

data class ProjectDto(
    @DocumentId var id: String = "",
    var name: String = "",
    var key: String? = null,
    var description: String? = null,
    var ownerEmail: String? = null
)

data class CreateProjectRequest(
    val name: String = "",
    val key: String = "",
    val description: String = ""
)

data class IssueDto(
    @DocumentId var id: String = "",
    var projectId: String = "",
    var title: String = "",
    var description: String? = null,
    var status: String = "TODO",
    var type: String = "TASK",
    var priority: String = "MEDIUM",
    var assignee: UserDto? = null,
    var dueDate: String? = null
)

data class CreateIssueRequest(
    val title: String = "",
    val description: String = "",
    val type: String = "TASK",
    val priority: String = "MEDIUM"
)

data class ProjectDetailDto(
    var id: String = "",
    var name: String = "",
    var description: String? = null,
    var members: List<UserDto> = emptyList()
)

data class UpdateStatusRequest(
    val status: String = ""
)

data class UserDto(
    @DocumentId var id: String = "",
    var name: String = "",
    var email: String = "",
    var password: String = "" // In a real app we'd use Firebase Auth, but we are keeping your structure
)
