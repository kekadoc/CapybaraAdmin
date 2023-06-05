package com.kekadoc.project.capybara.admin.domain.model.message

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import java.time.LocalDateTime

data class Message(
    val id: Identifier,
    val type: MessageType,
    val authorId: Identifier,
    val title: String?,
    val text: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val addresseeUserIds: List<Identifier>,
    val addresseeGroupIds: List<Identifier>,
    val actions: List<MessageAction>,
    val isMultiAnswer: Boolean,
    val notifications: MessageNotifications,
)