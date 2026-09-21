package com.jiralike.repository

import com.jiralike.domain.entity.PasswordResetToken
import com.jiralike.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Long> {
    fun findByToken(token: String): Optional<PasswordResetToken>

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user")
    fun deleteAllByUser(user: User)
}
