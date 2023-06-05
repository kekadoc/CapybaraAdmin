package com.kekadoc.project.capybara.admin.data.source.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokensRequest(
    val login: String,
    val refreshToken: String,
)