package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileTypeResponseDto(
    @SerialName("profile")
    val profile: ExtendedProfileDto,
)