package com.kekadoc.project.capybara.domain.model.message

data class MessageNotifications(
    val email: Boolean = false,
    val app: Boolean = false,
    val messengers: Boolean = false,
)