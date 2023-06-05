package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @Contextual
    @SerialName("id")
    val id: Identifier,
    @SerialName("name")
    val name: String,
    @SerialName("surname")
    val surname: String,
    @SerialName("patronymic")
    val patronymic: String,
    @SerialName("about")
    val about: String?,
)