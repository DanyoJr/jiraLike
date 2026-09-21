package com.jiralike.controller

import com.jiralike.dto.*
import com.jiralike.service.UserService
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
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management — Admin only (except change-password)")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create User (Admin)", description = "Create a new user. Only administrators can perform this action.")
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request))
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List All Users (Admin)", description = "Retrieve all users. Only administrators can perform this action.")
    fun findAll(): ResponseEntity<List<UserResponse>> {
        return ResponseEntity.ok(userService.findAll())
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get User by ID (Admin)")
    fun findById(@PathVariable id: Long): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.findById(id))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update User (Admin)", description = "Update user information. Only administrators can perform this action.")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.updateUser(id, request))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate User (Admin)", description = "Soft-delete a user (sets isActive=false). Only administrators can perform this action.")
    fun deactivateUser(@PathVariable id: Long): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(userService.deactivateUser(id))
    }

    @PostMapping("/me/change-password")
    @Operation(summary = "Change My Password", description = "Authenticated users can change their own password.")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Map<String, String>> {
        userService.changePassword(userDetails.username, request)
        return ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
    }
}
