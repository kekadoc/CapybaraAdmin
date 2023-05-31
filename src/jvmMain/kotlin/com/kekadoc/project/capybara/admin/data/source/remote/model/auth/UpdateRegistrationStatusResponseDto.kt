package com.kekadoc.project.capybara.server.routing.api.auth.model

import com.kekadoc.project.capybara.admin.data.source.remote.model.auth.RegistrationStatusDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRegistrationStatusResponseDto(
    @SerialName("status")
    val status: RegistrationStatusDto,
)