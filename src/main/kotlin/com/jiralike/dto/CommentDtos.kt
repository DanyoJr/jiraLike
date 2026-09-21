package com.jiralike.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

// ========== Comment DTOs ==========

data class CreateCommentRequest(
    @field:NotBlank(message = "Comment content is required")
    val content: String
)

data class UpdateCommentRequest(
    @field:NotBlank(message = "Comment content is required")
    val content: String
)

data class CommentResponse(
    val id: Long,
    val content: String,
    val author: UserResponse,
    val issueId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
