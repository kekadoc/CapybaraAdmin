package com.kekadoc.project.capybara.admin.data.source.remote.model.group

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupWithMembersDto(
    @SerialName("id")
    val id: Identifier,
    @SerialName("name")
    val name: String,
    @SerialName("members")
    val membersIds: List<Identifier>,
)