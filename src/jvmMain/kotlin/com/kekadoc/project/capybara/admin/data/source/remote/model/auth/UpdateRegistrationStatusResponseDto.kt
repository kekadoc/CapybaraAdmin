package com.kekadoc.project.capybara.admin.data.source.remote.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRegistrationStatusResponseDto(
    @SerialName("status")
    val status: RegistrationStatusDto,
)