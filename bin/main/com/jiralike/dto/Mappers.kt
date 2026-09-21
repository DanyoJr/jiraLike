package com.jiralike.dto

import com.jiralike.domain.entity.User

fun User.toResponse() = UserResponse(
    id = id,
    name = name,
    email = email,
    role = role,
    isActive = isActive,
    avatarUrl = avatarUrl,
    notifyNewTask = notifyNewTask,
    notifyComment = notifyComment,
    notifyMention = notifyMention,
    createdAt = createdAt
)
