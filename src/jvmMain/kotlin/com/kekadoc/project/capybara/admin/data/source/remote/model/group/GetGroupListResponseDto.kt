package com.kekadoc.project.capybara.admin.data.source.remote.model.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetGroupListResponseDto(
    @SerialName("groups")
    val groups: List<GroupDto>,
)