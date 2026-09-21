package com.jiralike.repository

import com.jiralike.domain.entity.Comment
import com.jiralike.domain.entity.Issue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByIssueOrderByCreatedAtAsc(issue: Issue): List<Comment>
}
