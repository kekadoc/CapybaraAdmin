package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.UserStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExtendedProfileDto(
    @Contextual
    @SerialName("id")
    val id: Identifier,
    @SerialName("status")
    val status: UserStatus,
    @SerialName("login")
    val login: String,
    @SerialName("type")
    val type: ProfileTypeDto,
    @SerialName("name")
    val name: String,
    @SerialName("surname")
    val surname: String,
    @SerialName("patronymic")
    val patronymic: String,
    @SerialName("about")
    val about: String?,
    @SerialName("communications")
    val communications: Map<String, Pair<String, Boolean?>>,
    @SerialName("group_ids")
    val groupIds: List<@Contextual Identifier>,
)