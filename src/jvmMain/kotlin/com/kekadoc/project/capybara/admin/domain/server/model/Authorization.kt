package com.kekadoc.project.capybara.server.domain.model

data class Authorization(
    val accessToken: String,
    val refreshToken: String,
)