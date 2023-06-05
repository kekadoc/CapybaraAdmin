package com.kekadoc.project.capybara.admin.data.source.remote.model.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MessageTypeDto {
    @SerialName("DEFAULT")
    DEFAULT,
    @SerialName("INFO")
    INFO,
    @SerialName("VOTE")
    VOTE
}