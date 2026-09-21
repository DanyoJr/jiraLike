package com.jiralike.controller

import com.jiralike.dto.*
import com.jiralike.service.ProjectService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Projects", description = "Project management endpoints")
class ProjectController(
    private val projectService: ProjectService
) {

    @PostMapping
    @Operation(summary = "Create Project", description = "Create a new project. Any authenticated user can create projects.")
    fun createProject(
        @Valid @RequestBody request: CreateProjectRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ProjectDetailResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(projectService.createProject(request, userDetails.username))
    }

    @GetMapping
    @Operation(summary = "List My Projects", description = "List all projects where the current user is a member or owner.")
    fun findMyProjects(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<ProjectSummaryResponse>> {
        return ResponseEntity.ok(projectService.findAllForUser(userDetails.username))
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List All Projects (Admin)", description = "List all active projects. Admin only.")
    fun findAll(): ResponseEntity<List<ProjectSummaryResponse>> {
        return ResponseEntity.ok(projectService.findAll())
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Project Details", description = "Get project details including members list.")
    fun findById(@PathVariable id: Long): ResponseEntity<ProjectDetailResponse> {
        return ResponseEntity.ok(projectService.findById(id))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Project", description = "Update project details. Only the project owner can do this.")
    fun updateProject(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProjectRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ProjectDetailResponse> {
        return ResponseEntity.ok(projectService.updateProject(id, request, userDetails.username))
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Add Member to Project", description = "Add a user to the project. Only the project owner can do this.")
    fun addMember(
        @PathVariable id: Long,
        @Valid @RequestBody request: AddMemberRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ProjectDetailResponse> {
        return ResponseEntity.ok(projectService.addMember(id, request, userDetails.username))
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove Member from Project", description = "Remove a user from the project. Only the project owner can do this.")
    fun removeMember(
        @PathVariable id: Long,
        @PathVariable userId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ProjectDetailResponse> {
        return ResponseEntity.ok(projectService.removeMember(id, userId, userDetails.username))
    }
}
