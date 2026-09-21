package com.jiralike.domain.entity

import com.jiralike.domain.enums.IssuePriority
import com.jiralike.domain.enums.IssueStatus
import com.jiralike.domain.enums.IssueType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "issues")
data class Issue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: IssueType = IssueType.TASK,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: IssueStatus = IssueStatus.TODO,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: IssuePriority = IssuePriority.MEDIUM,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    var project: Project,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    var reporter: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    var assignee: User? = null,

    @Column(name = "due_date")
    var dueDate: LocalDateTime? = null,

    @OneToMany(mappedBy = "issue", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val comments: MutableList<Comment> = mutableListOf(),

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
