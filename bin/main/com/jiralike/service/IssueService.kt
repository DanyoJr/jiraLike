package com.jiralike.service

import com.jiralike.domain.entity.Issue
import com.jiralike.domain.enums.IssueStatus
import com.jiralike.dto.*
import com.jiralike.exception.BadRequestException
import com.jiralike.exception.ForbiddenException
import com.jiralike.exception.ResourceNotFoundException
import com.jiralike.repository.IssueRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class IssueService(
    private val issueRepository: IssueRepository,
    private val projectService: ProjectService,
    private val userService: UserService
) {

    @Transactional
    fun createIssue(projectId: Long, request: CreateIssueRequest, reporterEmail: String): IssueResponse {
        val project = projectService.findEntityById(projectId)
        val reporter = userService.findEntityByEmail(reporterEmail)

        // Verify the reporter is a member of the project
        val isMember = project.members.any { it.id == reporter.id } || project.owner.id == reporter.id
        if (!isMember) {
            throw ForbiddenException("You must be a member of the project to create issues")
        }

        val assignee = request.assigneeId?.let { userService.findEntityById(it) }

        val issue = Issue(
            title = request.title,
            description = request.description,
            type = request.type,
            priority = request.priority,
            project = project,
            reporter = reporter,
            assignee = assignee
        )

        return issueRepository.save(issue).toResponse()
    }

    fun findAllByProject(projectId: Long, status: IssueStatus?): List<IssueResponse> {
        val project = projectService.findEntityById(projectId)
        val issues = if (status != null) {
            issueRepository.findAllByProjectAndStatus(project, status)
        } else {
            issueRepository.findAllByProject(project)
        }
        return issues.map { it.toResponse() }
    }

    fun findById(id: Long): IssueDetailResponse {
        val issue = issueRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Issue not found with id: $id") }
        return issue.toDetailResponse()
    }

    fun findEntityById(id: Long): Issue = issueRepository.findById(id)
        .orElseThrow { ResourceNotFoundException("Issue not found with id: $id") }

    fun findMyIssues(email: String): List<IssueResponse> {
        val user = userService.findEntityByEmail(email)
        return issueRepository.findAllByAssignee(user).map { it.toResponse() }
    }

    @Transactional
    fun updateIssue(id: Long, request: UpdateIssueRequest, requesterEmail: String): IssueResponse {
        val issue = findEntityById(id)
        val requester = userService.findEntityByEmail(requesterEmail)

        val isMember = issue.project.members.any { it.id == requester.id } ||
                issue.project.owner.id == requester.id
        if (!isMember) {
            throw ForbiddenException("You must be a member of the project to edit issues")
        }

        request.title?.let { issue.title = it }
        request.description?.let { issue.description = it }
        request.type?.let { issue.type = it }
        request.priority?.let { issue.priority = it }
        request.assigneeId?.let { issue.assignee = userService.findEntityById(it) }
        issue.updatedAt = LocalDateTime.now()

        return issueRepository.save(issue).toResponse()
    }

    @Transactional
    fun updateStatus(id: Long, request: UpdateIssueStatusRequest, requesterEmail: String): IssueResponse {
        val issue = findEntityById(id)
        val requester = userService.findEntityByEmail(requesterEmail)

        val isMember = issue.project.members.any { it.id == requester.id } ||
                issue.project.owner.id == requester.id
        if (!isMember) {
            throw ForbiddenException("You must be a member of the project to update issue status")
        }

        issue.status = request.status
        issue.updatedAt = LocalDateTime.now()

        return issueRepository.save(issue).toResponse()
    }

    @Transactional
    fun deleteIssue(id: Long, requesterEmail: String) {
        val issue = findEntityById(id)
        val requester = userService.findEntityByEmail(requesterEmail)

        val isOwnerOrReporter = issue.reporter.id == requester.id ||
                issue.project.owner.id == requester.id
        if (!isOwnerOrReporter) {
            throw ForbiddenException("Only the reporter or project owner can delete an issue")
        }

        issueRepository.delete(issue)
    }

    private fun Issue.toResponse() = IssueResponse(
        id = id,
        title = title,
        description = description,
        type = type,
        status = status,
        priority = priority,
        projectId = project.id,
        projectKey = project.key,
        reporter = reporter.toResponse(),
        assignee = assignee?.toResponse(),
        dueDate = dueDate,
        commentCount = comments.size,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Issue.toDetailResponse() = IssueDetailResponse(
        id = id,
        title = title,
        description = description,
        type = type,
        status = status,
        priority = priority,
        projectId = project.id,
        projectKey = project.key,
        reporter = reporter.toResponse(),
        assignee = assignee?.toResponse(),
        dueDate = dueDate,
        comments = comments.map { c ->
            CommentResponse(
                id = c.id,
                content = c.content,
                author = c.author.toResponse(),
                issueId = id,
                createdAt = c.createdAt,
                updatedAt = c.updatedAt
            )
        },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
