package com.jiralike.controller

import com.jiralike.domain.enums.IssueStatus
import com.jiralike.dto.*
import com.jiralike.service.CommentService
import com.jiralike.service.IssueService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Issues", description = "Issue and comment management")
class IssueController(
    private val issueService: IssueService,
    private val commentService: CommentService
) {

    // ===== Issues =====

    @PostMapping("/api/projects/{projectId}/issues")
    @Operation(summary = "Create Issue", description = "Create a new issue in a project. User must be a project member.")
    fun createIssue(
        @PathVariable projectId: Long,
        @Valid @RequestBody request: CreateIssueRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<IssueResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(issueService.createIssue(projectId, request, userDetails.username))
    }

    @GetMapping("/api/projects/{projectId}/issues")
    @Operation(summary = "List Issues by Project", description = "Get all issues for a project. Optionally filter by status.")
    fun findAllByProject(
        @PathVariable projectId: Long,
        @RequestParam(required = false) status: IssueStatus?
    ): ResponseEntity<List<IssueResponse>> {
        return ResponseEntity.ok(issueService.findAllByProject(projectId, status))
    }

    @GetMapping("/api/issues/{id}")
    @Operation(summary = "Get Issue Details", description = "Get full issue details including all comments.")
    fun findById(@PathVariable id: Long): ResponseEntity<IssueDetailResponse> {
        return ResponseEntity.ok(issueService.findById(id))
    }

    @GetMapping("/api/issues/my")
    @Operation(summary = "My Assigned Issues", description = "Get all issues assigned to the current user.")
    fun findMyIssues(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<IssueResponse>> {
        return ResponseEntity.ok(issueService.findMyIssues(userDetails.username))
    }

    @PutMapping("/api/issues/{id}")
    @Operation(summary = "Update Issue", description = "Update issue fields. User must be a project member.")
    fun updateIssue(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateIssueRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<IssueResponse> {
        return ResponseEntity.ok(issueService.updateIssue(id, request, userDetails.username))
    }

    @PatchMapping("/api/issues/{id}/status")
    @Operation(summary = "Update Issue Status", description = "Move an issue through the Kanban stages: TODO → IN_PROGRESS → IN_REVIEW → DONE.")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateIssueStatusRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<IssueResponse> {
        return ResponseEntity.ok(issueService.updateStatus(id, request, userDetails.username))
    }

    @DeleteMapping("/api/issues/{id}")
    @Operation(summary = "Delete Issue", description = "Delete an issue. Only the reporter or project owner can do this.")
    fun deleteIssue(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Map<String, String>> {
        issueService.deleteIssue(id, userDetails.username)
        return ResponseEntity.ok(mapOf("message" to "Issue deleted successfully"))
    }

    // ===== Comments =====

    @PostMapping("/api/issues/{issueId}/comments")
    @Operation(summary = "Add Comment", description = "Add a comment to an issue.")
    fun addComment(
        @PathVariable issueId: Long,
        @Valid @RequestBody request: CreateCommentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<CommentResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.addComment(issueId, request, userDetails.username))
    }

    @GetMapping("/api/issues/{issueId}/comments")
    @Operation(summary = "List Comments", description = "Get all comments for an issue, ordered by date.")
    fun findCommentsByIssue(@PathVariable issueId: Long): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity.ok(commentService.findAllByIssue(issueId))
    }

    @PutMapping("/api/comments/{id}")
    @Operation(summary = "Update Comment", description = "Edit a comment. Only the comment author can do this.")
    fun updateComment(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateCommentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<CommentResponse> {
        return ResponseEntity.ok(commentService.updateComment(id, request, userDetails.username))
    }

    @DeleteMapping("/api/comments/{id}")
    @Operation(summary = "Delete Comment", description = "Delete a comment. Only the author or project owner can do this.")
    fun deleteComment(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Map<String, String>> {
        commentService.deleteComment(id, userDetails.username)
        return ResponseEntity.ok(mapOf("message" to "Comment deleted successfully"))
    }
}
