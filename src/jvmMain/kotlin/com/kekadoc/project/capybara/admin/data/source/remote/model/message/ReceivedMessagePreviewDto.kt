package com.kekadoc.project.capybara.admin.data.source.remote.model.message

import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ProfileDto
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceivedMessagePreviewDto(
    @Contextual
    @SerialName("id")
    val id: Identifier,
    @SerialName("type")
    val type: MessageTypeDto,
    @SerialName("author")
    val author: ProfileDto,
    @SerialName("title")
    val title: String?,
    @SerialName("message")
    val message: String,
    @SerialName("date")
    val date: String,
    @SerialName("read")
    val read: Boolean,
    @SerialName("answer_ids")
    val answerIds: List<Long>?,
    @SerialName("actions")
    val actions: List<MessageActionDto>,
    @SerialName("is_multi_answer")
    val isMultiAnswer: Boolean,
)