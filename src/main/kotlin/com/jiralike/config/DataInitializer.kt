package com.jiralike.config

import com.jiralike.domain.entity.User
import com.jiralike.domain.enums.Role
import com.jiralike.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String?) {
        ensureAdmin()
        ensureDefaultUser()
    }

    private fun ensureAdmin() {
        val email = "admin@jiralike.com"
        val existing = userRepository.findByEmail(email)

        if (existing.isPresent) {
            // Atualiza a senha garantindo o hash correto
            val admin = existing.get()
            admin.password = passwordEncoder.encode("Admin@123")
            userRepository.save(admin)
            log.info("✅ Admin atualizado: $email / Admin@123")
        } else {
            userRepository.save(
                User(
                    name = "Administrator",
                    email = email,
                    password = passwordEncoder.encode("Admin@123"),
                    role = Role.ADMIN
                )
            )
            log.info("✅ Admin criado: $email / Admin@123")
        }
    }

    private fun ensureDefaultUser() {
        val email = "user@jiralike.com"
        val existing = userRepository.findByEmail(email)

        if (existing.isPresent) {
            val user = existing.get()
            user.password = passwordEncoder.encode("User@123")
            userRepository.save(user)
            log.info("✅ Usuário atualizado: $email / User@123")
        } else {
            userRepository.save(
                User(
                    name = "João Silva",
                    email = email,
                    password = passwordEncoder.encode("User@123"),
                    role = Role.USER
                )
            )
            log.info("✅ Usuário criado: $email / User@123")
        }
    }
}
