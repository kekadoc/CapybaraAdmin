package com.kekadoc.project.capybara.admin.data.source.remote.model.message

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SentMessagePreviewDto(
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
    @SerialName("actions")
    val actions: List<MessageActionSimpleStatisticDto>,
    @SerialName("is_multi_answer")
    val isMultiAnswer: Boolean,
    @SerialName("addressee_group_ids")
    val addresseeGroupIds: List<@Contextual Identifier>,
    @SerialName("addressee_user_ids")
    val addresseeUsersIds: List<@Contextual Identifier>,
    @SerialName("status")
    val status: MessageStatusDto,
)