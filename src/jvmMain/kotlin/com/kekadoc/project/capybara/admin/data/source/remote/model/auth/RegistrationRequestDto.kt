package com.kekadoc.project.capybara.server.routing.api.auth.model

import com.kekadoc.project.capybara.server.domain.model.Identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequestDto(
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