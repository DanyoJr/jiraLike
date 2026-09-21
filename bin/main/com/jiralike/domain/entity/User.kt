package com.jiralike.domain.entity

import com.jiralike.domain.enums.Role
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.USER,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column
    var avatarUrl: String? = null,

    @Column(nullable = false)
    var notifyNewTask: Boolean = true,

    @Column(nullable = false)
    var notifyComment: Boolean = true,

    @Column(nullable = false)
    var notifyMention: Boolean = false,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    val projects: MutableSet<Project> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return id != 0L && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
