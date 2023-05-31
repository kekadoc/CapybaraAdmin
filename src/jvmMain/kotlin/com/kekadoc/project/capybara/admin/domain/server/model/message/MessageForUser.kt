package com.kekadoc.project.capybara.server.domain.model.message

import com.kekadoc.project.capybara.server.domain.model.Identifier

data class MessageForUser(
    val id: Identifier,
    val messageId: Identifier,
    val userId: Identifier,
    val received: Boolean,
    val read: Boolean,
    val answerIds: List<Long>?,
    val fromGroup: Boolean,
)