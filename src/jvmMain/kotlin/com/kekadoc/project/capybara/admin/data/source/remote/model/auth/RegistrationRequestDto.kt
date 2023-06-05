package com.kekadoc.project.capybara.admin.data.source.remote.model.auth

import com.kekadoc.project.capybara.admin.domain.model.Identifier
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