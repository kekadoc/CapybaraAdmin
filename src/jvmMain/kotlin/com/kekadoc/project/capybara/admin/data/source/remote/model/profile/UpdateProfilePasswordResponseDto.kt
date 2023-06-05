package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ExtendedProfileDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfilePasswordResponseDto(
    @SerialName("profile")
    val profile: ExtendedProfileDto,
)