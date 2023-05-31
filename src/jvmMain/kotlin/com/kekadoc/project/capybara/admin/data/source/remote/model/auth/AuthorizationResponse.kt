package com.kekadoc.project.capybara.admin.data.source.remote.model.auth

import com.kekadoc.project.capybara.server.domain.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationResponse(
    @SerialName("accessToken")
    val accessToken: Token,
    @SerialName("refreshToken")
    val refreshToken: Token,
)