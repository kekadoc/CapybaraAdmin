package com.kekadoc.project.capybara.server.domain.model

data class Group(
    val id: Identifier,
    val name: String,
    val members: List<User>,
)