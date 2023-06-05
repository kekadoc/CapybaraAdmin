package com.kekadoc.project.capybara.admin.data.source.remote.model.message

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentMessageInfoDto(
    @Contextual
    @SerialName("id")
    val id: Identifier,
    @SerialName("type")
    val type: MessageTypeDto,
    @SerialName("title")
    val title: String?,
    @SerialName("text")
    val text: String,
    @SerialName("date")
    val date: String,
    @SerialName("answers")
    val actions: List<MessageActionDto>,
    @SerialName("is_multi_answer")
    val isMultiAnswer: Boolean,
    @SerialName("addressee_groups")
    val addresseeGroups: List<GroupInfo>,
    @SerialName("addressee_users")
    val addresseeUsers: List<FromUserInfo>,
    @SerialName("status")
    val status: MessageStatusDto,
    @SerialName("notifications")
    val notifications: MessageNotificationsDto,
) {

    @Serializable
    data class GroupInfo(
        @Contextual
        @SerialName("group_id")
        val groupId: Identifier,
        @SerialName("name")
        val name: String,
        @SerialName("members")
        val members: List<FromUserInfo>,
    )

    @Serializable
    data class FromUserInfo(
        @Contextual
        @SerialName("user_id")
        val userId: Identifier,
        @SerialName("received")
        val received: Boolean,
        @SerialName("read")
        val read: Boolean,
        @SerialName("answer_ids")
        val answer: List<Long>?,
    )

}