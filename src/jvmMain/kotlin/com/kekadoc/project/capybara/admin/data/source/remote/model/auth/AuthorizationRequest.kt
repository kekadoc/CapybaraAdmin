package com.kekadoc.project.capybara.admin.data.source.remote.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationRequest(
    val login: String,
    val password: String,
)