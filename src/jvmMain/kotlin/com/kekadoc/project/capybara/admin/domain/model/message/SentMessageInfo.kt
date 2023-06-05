package com.kekadoc.project.capybara.admin.domain.model.message

import java.time.ZonedDateTime

data class SentMessageInfo(
    val id: String,
    val type: MessageType,
    val title: String?,
    val text: String,
    val date: ZonedDateTime,
    val actions: List<MessageAction>,
    val isMultiAnswer: Boolean,
    val addresseeGroups: List<GroupInfo>,
    val addresseeUsers: List<FromUserInfo>,
    val status: MessageStatus,
    val notifications: MessageNotifications,
) {

    data class GroupInfo(
        val groupId: String,
        val name: String,
        val members: List<FromUserInfo>,
    )

    data class FromUserInfo(
        val userId: String,
        val received: Boolean,
        val read: Boolean,
        val answer: List<Long>?,
    )

}