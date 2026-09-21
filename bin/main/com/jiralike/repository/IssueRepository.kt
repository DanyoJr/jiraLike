package com.jiralike.repository

import com.jiralike.domain.entity.Issue
import com.jiralike.domain.entity.Project
import com.jiralike.domain.entity.User
import com.jiralike.domain.enums.IssueStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IssueRepository : JpaRepository<Issue, Long> {
    fun findAllByProject(project: Project): List<Issue>
    fun findAllByProjectAndStatus(project: Project, status: IssueStatus): List<Issue>
    fun findAllByAssignee(assignee: User): List<Issue>
    fun findAllByReporter(reporter: User): List<Issue>
}
