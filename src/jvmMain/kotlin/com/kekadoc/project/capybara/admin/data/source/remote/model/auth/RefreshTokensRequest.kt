package com.kekadoc.project.capybara.server.routing.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokensRequest(
    val login: String,
    val refreshToken: String,
)