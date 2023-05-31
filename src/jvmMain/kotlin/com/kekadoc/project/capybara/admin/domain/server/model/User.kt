package com.kekadoc.project.capybara.server.domain.model

data class User(
    val id: Identifier,
    val status: UserStatus,
    val login: String,
    val password: String,
    val profile: Profile,
    val communications: Communications,
    val groupIds: List<Identifier>
)

fun User.isAdmin(): Boolean = profile.type == Profile.Type.ADMIN