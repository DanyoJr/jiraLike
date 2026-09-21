package com.jiralike.controller

import com.jiralike.dto.*
import com.jiralike.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login, password recovery, and profile endpoints")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with email and password. Returns a JWT Bearer token.")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.login(request))
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Create a new user account.")
    fun register(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @PostMapping("/forgot-password")
    @Operation(
        summary = "Forgot Password",
        description = "Request a password reset token. The token is returned in the response (simulated — in production it would be sent via email)."
    )
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordRequest): ResponseEntity<ForgotPasswordResponse> {
        return ResponseEntity.ok(authService.forgotPassword(request))
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset password using the token received from the forgot-password endpoint.")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest): ResponseEntity<Map<String, String>> {
        authService.resetPassword(request)
        return ResponseEntity.ok(mapOf("message" to "Password reset successfully. You can now login with your new password."))
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User", description = "Get the currently authenticated user's profile.")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(authService.getCurrentUser(userDetails.username))
    }
}
