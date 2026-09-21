package com.jiralike.service

import com.jiralike.domain.entity.PasswordResetToken
import com.jiralike.domain.entity.User
import com.jiralike.dto.*
import com.jiralike.exception.BadRequestException
import com.jiralike.exception.ResourceNotFoundException
import com.jiralike.exception.UnauthorizedException
import com.jiralike.repository.PasswordResetTokenRepository
import com.jiralike.repository.UserRepository
import com.jiralike.security.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService
) {

    @Transactional
    fun register(request: CreateUserRequest): UserResponse {
        return userService.createUser(request)
    }

    fun login(request: LoginRequest): TokenResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UnauthorizedException("User not found") }

        val token = jwtUtil.generateToken(user.id, user.email, user.role.name)

        return TokenResponse(
            accessToken = token,
            userId = user.id,
            name = user.name,
            email = user.email,
            role = user.role
        )
    }

    @Transactional
    fun forgotPassword(request: ForgotPasswordRequest): ForgotPasswordResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { ResourceNotFoundException("No account found with email: ${request.email}") }

        // Invalidate old tokens
        passwordResetTokenRepository.deleteAllByUser(user)

        val rawToken = UUID.randomUUID().toString()
        val resetToken = PasswordResetToken(
            token = rawToken,
            user = user,
            expiresAt = LocalDateTime.now().plusHours(1)
        )
        passwordResetTokenRepository.save(resetToken)

        return ForgotPasswordResponse(
            message = "Password reset token generated. Use the token below to reset your password.",
            resetToken = rawToken
        )
    }

    @Transactional
    fun resetPassword(request: ResetPasswordRequest) {
        if (request.newPassword != request.confirmPassword) {
            throw BadRequestException("Passwords do not match")
        }

        val resetToken = passwordResetTokenRepository.findByToken(request.token)
            .orElseThrow { BadRequestException("Invalid or expired reset token") }

        if (resetToken.used) {
            throw BadRequestException("This reset token has already been used")
        }

        if (resetToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw BadRequestException("Reset token has expired. Please request a new one")
        }

        val user = resetToken.user
        user.password = passwordEncoder.encode(request.newPassword)
        user.updatedAt = LocalDateTime.now()
        userRepository.save(user)

        resetToken.used = true
        passwordResetTokenRepository.save(resetToken)
    }

    fun getCurrentUser(email: String): UserResponse {
        val user = userRepository.findByEmail(email)
            .orElseThrow { ResourceNotFoundException("User not found") }
        return user.toResponse()
    }
}
