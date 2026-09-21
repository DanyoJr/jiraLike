package com.jiralike.dto

import com.jiralike.domain.enums.IssuePriority
import com.jiralike.domain.enums.IssueStatus
import com.jiralike.domain.enums.IssueType
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

// ========== Issue DTOs ==========

data class CreateIssueRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    val description: String? = null,
    val type: IssueType = IssueType.TASK,
    val priority: IssuePriority = IssuePriority.MEDIUM,
    val assigneeId: Long? = null,
    val dueDate: LocalDateTime? = null
)

data class UpdateIssueRequest(
    val title: String? = null,
    val description: String? = null,
    val type: IssueType? = null,
    val priority: IssuePriority? = null,
    val assigneeId: Long? = null,
    val dueDate: LocalDateTime? = null
)

data class UpdateIssueStatusRequest(
    val status: IssueStatus
)

data class IssueResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val type: IssueType,
    val status: IssueStatus,
    val priority: IssuePriority,
    val projectId: Long,
    val projectKey: String,
    val reporter: UserResponse,
    val assignee: UserResponse?,
    val dueDate: LocalDateTime?,
    val commentCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class IssueDetailResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val type: IssueType,
    val status: IssueStatus,
    val priority: IssuePriority,
    val projectId: Long,
    val projectKey: String,
    val reporter: UserResponse,
    val assignee: UserResponse?,
    val dueDate: LocalDateTime?,
    val comments: List<CommentResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
