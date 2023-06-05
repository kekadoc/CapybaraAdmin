package com.kekadoc.project.capybara.admin.data.source.remote.model.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupResponseDto(
    @SerialName("group")
    val group: GroupWithMembersDto
)