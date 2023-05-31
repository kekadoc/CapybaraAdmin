package com.kekadoc.project.capybara.domain.model.auth

import com.kekadoc.project.capybara.admin.domain.model.group.Group

data class RegistrationInfo(
    val id: String,
    val name: String,
    val surname: String,
    val patronymic: String,
    val email: String,
    val isStudent: Boolean,
    val group: Group?,
    val status: RegistrationStatus,
)