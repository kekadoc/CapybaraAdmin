package com.kekadoc.project.capybara.server.routing.api.auth.model

import com.kekadoc.project.capybara.admin.data.source.remote.model.auth.RegistrationStatusDto
import com.kekadoc.project.capybara.server.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationStatusResponseDto(
    @Contextual
    @SerialName("id")
    val id: Identifier,
    @SerialName("status")
    val status: RegistrationStatusDto,
)