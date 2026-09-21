package com.jiralike.service

import com.jiralike.domain.entity.Comment
import com.jiralike.dto.*
import com.jiralike.exception.ForbiddenException
import com.jiralike.exception.ResourceNotFoundException
import com.jiralike.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val issueService: IssueService,
    private val userService: UserService
) {

    @Transactional
    fun addComment(issueId: Long, request: CreateCommentRequest, authorEmail: String): CommentResponse {
        val issue = issueService.findEntityById(issueId)
        val author = userService.findEntityByEmail(authorEmail)

        val isMember = issue.project.members.any { it.id == author.id } ||
                issue.project.owner.id == author.id
        if (!isMember) {
            throw ForbiddenException("You must be a member of the project to comment on issues")
        }

        val comment = Comment(
            content = request.content,
            issue = issue,
            author = author
        )

        return commentRepository.save(comment).toResponse()
    }

    fun findAllByIssue(issueId: Long): List<CommentResponse> {
        val issue = issueService.findEntityById(issueId)
        return commentRepository.findAllByIssueOrderByCreatedAtAsc(issue).map { it.toResponse() }
    }

    @Transactional
    fun updateComment(id: Long, request: UpdateCommentRequest, requesterEmail: String): CommentResponse {
        val comment = commentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Comment not found with id: $id") }
        val requester = userService.findEntityByEmail(requesterEmail)

        if (comment.author.id != requester.id) {
            throw ForbiddenException("You can only edit your own comments")
        }

        comment.content = request.content
        comment.updatedAt = LocalDateTime.now()

        return commentRepository.save(comment).toResponse()
    }

    @Transactional
    fun deleteComment(id: Long, requesterEmail: String) {
        val comment = commentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Comment not found with id: $id") }
        val requester = userService.findEntityByEmail(requesterEmail)

        val isAuthorOrOwner = comment.author.id == requester.id ||
                comment.issue.project.owner.id == requester.id
        if (!isAuthorOrOwner) {
            throw ForbiddenException("You can only delete your own comments or be the project owner")
        }

        commentRepository.delete(comment)
    }

    private fun Comment.toResponse() = CommentResponse(
        id = id,
        content = content,
        author = author.toResponse(),
        issueId = issue.id,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
