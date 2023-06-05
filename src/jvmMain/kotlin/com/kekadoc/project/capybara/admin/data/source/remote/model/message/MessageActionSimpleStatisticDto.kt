package com.kekadoc.project.capybara.admin.data.source.remote.model.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageActionSimpleStatisticDto(
    @SerialName("action")
    val action: MessageActionDto,
    @SerialName("select_count")
    val selectCount: Int,
)