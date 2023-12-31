package com.kekadoc.project.capybara.admin.domain.model.message

import java.time.ZonedDateTime

data class SentMessagePreview(
    val id: String,
    val type: MessageType,
    val title: String?,
    val text: String,
    val date: ZonedDateTime,
    val actions: Map<MessageAction, Int>,
    val isMultiAnswer: Boolean,
    val addresseeGroupIds: List<String>,
    val addresseeUsersIds: List<String>,
    val status: MessageStatus,
)