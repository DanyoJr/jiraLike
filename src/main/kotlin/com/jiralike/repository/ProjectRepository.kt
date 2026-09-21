package com.jiralike.repository

import com.jiralike.domain.entity.Project
import com.jiralike.domain.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
    fun existsByKey(key: String): Boolean
    fun findByKey(key: String): Optional<Project>

    @Query("SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.members")
    fun findAllByMemberOrOwner(user: User): List<Project>

    fun findAllByIsActiveTrue(): List<Project>
}
