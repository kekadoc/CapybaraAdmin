package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunicationDto(
    @SerialName("type")
    val type: String,
    @SerialName("value")
    val value: String,
    @SerialName("approved")
    val approved: Boolean?,
)