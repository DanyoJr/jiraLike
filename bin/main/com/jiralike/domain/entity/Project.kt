package com.jiralike.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "projects")
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true, length = 10, name = "\"key\"")
    var key: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    var owner: User,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "project_members",
        joinColumns = [JoinColumn(name = "project_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val members: MutableSet<User> = mutableSetOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val issues: MutableList<Issue> = mutableListOf(),

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Project
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
