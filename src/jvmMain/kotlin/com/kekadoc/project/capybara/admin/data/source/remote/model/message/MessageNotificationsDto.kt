package com.kekadoc.project.capybara.admin.data.source.remote.model.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageNotificationsDto(
    @SerialName("email")
    val email: Boolean = false,
    @SerialName("app")
    val app: Boolean = false,
    @SerialName("messengers")
    val messengers: Boolean = false,
)