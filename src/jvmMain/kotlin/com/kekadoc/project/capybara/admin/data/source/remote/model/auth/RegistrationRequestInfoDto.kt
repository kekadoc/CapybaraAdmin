package com.kekadoc.project.capybara.admin.data.source.remote.model.auth

import com.kekadoc.project.capybara.admin.data.source.remote.model.auth.RegistrationStatusDto
import com.kekadoc.project.capybara.server.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequestInfoDto(
    @Contextual
    @SerialName("id")
    val id: Identifier,
    @SerialName("status")
    val status: String,
    @SerialName("name")
    val name: String,
    @SerialName("surname")
    val surname: String,
    @SerialName("patronymic")
    val patronymic: String,
    @SerialName("email")
    val email: String,
    @SerialName("is_student")
    val isStudent: Boolean,
    @Contextual
    @SerialName("group_id")
    val groupId: Identifier?,
)