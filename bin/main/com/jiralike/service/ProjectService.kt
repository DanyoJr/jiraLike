package com.jiralike.service

import com.jiralike.domain.entity.Project
import com.jiralike.dto.*
import com.jiralike.exception.BadRequestException
import com.jiralike.exception.ConflictException
import com.jiralike.exception.ForbiddenException
import com.jiralike.exception.ResourceNotFoundException
import com.jiralike.repository.ProjectRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val userService: UserService
) {

    @Transactional
    fun createProject(request: CreateProjectRequest, ownerEmail: String): ProjectDetailResponse {
        if (projectRepository.existsByKey(request.key)) {
            throw ConflictException("Project key '${request.key}' already exists")
        }

        val owner = userService.findEntityByEmail(ownerEmail)
        val project = Project(
            name = request.name,
            key = request.key,
            description = request.description,
            owner = owner
        )
        // Ensure lazy collections are initialized properly for response
        project.members.add(owner)
        owner.projects.add(project)

        val savedProject = projectRepository.saveAndFlush(project)
        return savedProject.toDetailResponse()
    }

    fun findAllForUser(email: String): List<ProjectSummaryResponse> {
        val user = userService.findEntityByEmail(email)
        return projectRepository.findAllByMemberOrOwner(user).map { it.toSummaryResponse() }
    }

    fun findAll(): List<ProjectSummaryResponse> =
        projectRepository.findAllByIsActiveTrue().map { it.toSummaryResponse() }

    fun findById(id: Long): ProjectDetailResponse {
        val project = projectRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Project not found with id: $id") }
        return project.toDetailResponse()
    }

    fun findEntityById(id: Long): Project = projectRepository.findById(id)
        .orElseThrow { ResourceNotFoundException("Project not found with id: $id") }

    @Transactional
    fun updateProject(id: Long, request: UpdateProjectRequest, email: String): ProjectDetailResponse {
        val project = findEntityById(id)
        val requestingUser = userService.findEntityByEmail(email)

        if (project.owner.id != requestingUser.id) {
            throw ForbiddenException("Only the project owner can update project details")
        }

        request.name?.let { project.name = it }
        request.description?.let { project.description = it }
        request.isActive?.let { project.isActive = it }
        project.updatedAt = LocalDateTime.now()

        return projectRepository.save(project).toDetailResponse()
    }

    @Transactional
    fun addMember(projectId: Long, request: AddMemberRequest, requesterEmail: String): ProjectDetailResponse {
        val project = findEntityById(projectId)
        val requester = userService.findEntityByEmail(requesterEmail)

        if (project.owner.id != requester.id) {
            throw ForbiddenException("Only the project owner can add members")
        }

        val newMember = userService.findEntityById(request.userId)

        if (!newMember.isActive) {
            throw BadRequestException("Cannot add an inactive user to a project")
        }

        if (project.members.any { it.id == newMember.id }) {
            throw ConflictException("User is already a member of this project")
        }

        project.members.add(newMember)
        project.updatedAt = LocalDateTime.now()

        return projectRepository.save(project).toDetailResponse()
    }

    @Transactional
    fun removeMember(projectId: Long, userId: Long, requesterEmail: String): ProjectDetailResponse {
        val project = findEntityById(projectId)
        val requester = userService.findEntityByEmail(requesterEmail)

        if (project.owner.id != requester.id) {
            throw ForbiddenException("Only the project owner can remove members")
        }

        if (project.owner.id == userId) {
            throw BadRequestException("Cannot remove the project owner from the project")
        }

        val memberToRemove = project.members.find { it.id == userId }
            ?: throw ResourceNotFoundException("User with id $userId is not a member of this project")

        project.members.remove(memberToRemove)
        project.updatedAt = LocalDateTime.now()

        return projectRepository.save(project).toDetailResponse()
    }

    private fun Project.toSummaryResponse() = ProjectSummaryResponse(
        id = id,
        name = name,
        key = key,
        description = description,
        ownerName = owner.name,
        memberCount = members.size,
        issueCount = issues.size,
        isActive = isActive,
        createdAt = createdAt
    )

    private fun Project.toDetailResponse() = ProjectDetailResponse(
        id = id,
        name = name,
        key = key,
        description = description,
        owner = owner.toResponse(),
        members = members.map { it.toResponse() },
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
