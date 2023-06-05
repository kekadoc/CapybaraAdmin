package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProfileResponseDto(
    @SerialName("profile")
    val profile: ExtendedProfileDto,
    @SerialName("temp_pass")
    val tempPassword: String,
)