package com.jiralike.service

import com.jiralike.domain.entity.User
import com.jiralike.dto.*
import com.jiralike.exception.BadRequestException
import com.jiralike.exception.ConflictException
import com.jiralike.exception.ResourceNotFoundException
import com.jiralike.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("Email already in use: ${request.email}")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = request.role
        )

        return userRepository.save(user).toResponse()
    }

    fun findAll(): List<UserResponse> = userRepository.findAll().map { it.toResponse() }

    fun findById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        return user.toResponse()
    }

    fun findEntityById(id: Long): User = userRepository.findById(id)
        .orElseThrow { ResourceNotFoundException("User not found with id: $id") }

    fun findEntityByEmail(email: String): User = userRepository.findByEmail(email)
        .orElseThrow { ResourceNotFoundException("User not found with email: $email") }

    @Transactional
    fun updateUser(id: Long, request: UpdateUserRequest): UserResponse {
        val user = findEntityById(id)

        request.name?.let { user.name = it }
        request.email?.let { newEmail ->
            if (newEmail != user.email && userRepository.existsByEmail(newEmail)) {
                throw ConflictException("Email already in use: $newEmail")
            }
            user.email = newEmail
        }
        request.role?.let { user.role = it }
        request.isActive?.let { user.isActive = it }
        user.updatedAt = LocalDateTime.now()

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun deactivateUser(id: Long): UserResponse {
        val user = findEntityById(id)
        user.isActive = false
        user.updatedAt = LocalDateTime.now()
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun changePassword(email: String, request: ChangePasswordRequest) {
        val user = findEntityByEmail(email)

        if (!passwordEncoder.matches(request.currentPassword, user.password)) {
            throw BadRequestException("Current password is incorrect")
        }

        user.password = passwordEncoder.encode(request.newPassword)
        user.updatedAt = LocalDateTime.now()
        userRepository.save(user)
    }
}
