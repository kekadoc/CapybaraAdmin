package com.kekadoc.project.capybara.admin.domain.model.profile

import com.kekadoc.project.capybara.admin.domain.model.Identifier

data class ExtendedProfile(
    val id: Identifier,
    val status: Status,
    val login: String,
    val type: Type,
    val name: String,
    val surname: String,
    val patronymic: String,
    val about: String?,
    val communications: List<Communication>,
    val groupIds: List<String>,
) {

    enum class Type {
        USER,
        SPEAKER,
        ADMIN,
    }

    enum class Status {

        ACTIVE,
        BLOCKED,
        NEED_UPDATE_PASSWORD;

        val isActive: Boolean
            get() = this == ACTIVE

        val isBlocked: Boolean
            get() = this == BLOCKED

        val isNeedUpdatePassword: Boolean
            get() = this == NEED_UPDATE_PASSWORD

    }

}

fun ExtendedProfile.short(): ShortProfile {
    return ShortProfile(
        id = id,
        name = name,
        surname = surname,
        patronymic = patronymic,
        about = about,
    )
}