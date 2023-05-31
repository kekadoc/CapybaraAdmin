package com.kekadoc.project.capybara.admin.domain.model.auth

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
)