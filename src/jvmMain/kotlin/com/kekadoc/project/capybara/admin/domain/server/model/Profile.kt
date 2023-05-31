package com.kekadoc.project.capybara.server.domain.model

data class Profile(
    val type: Type,
    val name: String,
    val surname: String,
    val patronymic: String,
    val about: String?,
) {

    enum class Type {
        USER,
        SPEAKER,
        ADMIN,
        DEFAULT,
    }

}