package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateProfileRequestDto(
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
    @SerialName("login")
    val login: String? = null,
    @SerialName("password")
    val password: String? = null,
    @SerialName("email_for_invite")
    val emailForInvite: String? = null,
)