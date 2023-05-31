package com.kekadoc.project.capybara.server.domain.model.message

import com.kekadoc.project.capybara.server.domain.model.Identifier

data class MessageInfo(
    val message: Message,
    val addresseeGroups: List<GroupInfo>,
    val addresseeUsers: List<FromUserInfo>,
    val status: MessageStatus,
) {

    data class GroupInfo(
        val groupId: Identifier,
        val name: String,
        val members: List<FromUserInfo>,
    )

    data class FromUserInfo(
        val userId: Identifier,
        val received: Boolean,
        val read: Boolean,
        val answerIds: List<Long>?,
    )

}