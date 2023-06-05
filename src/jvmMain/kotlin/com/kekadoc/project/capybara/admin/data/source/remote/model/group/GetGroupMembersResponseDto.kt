package com.kekadoc.project.capybara.admin.data.source.remote.model.group

import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ProfileDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetGroupMembersResponseDto(
    @SerialName("members")
    val members: List<ProfileDto>
)