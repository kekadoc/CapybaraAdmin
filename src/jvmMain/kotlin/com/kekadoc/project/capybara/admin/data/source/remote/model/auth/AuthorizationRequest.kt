package com.kekadoc.project.capybara.server.routing.api.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationRequest(
    val login: String,
    val password: String,
)