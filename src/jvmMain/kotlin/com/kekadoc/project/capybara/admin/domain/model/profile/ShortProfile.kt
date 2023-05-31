package com.kekadoc.project.capybara.admin.domain.model.profile

import com.kekadoc.project.capybara.admin.domain.model.Identifier

data class ShortProfile(
    val id: Identifier,
    val name: String,
    val surname: String,
    val patronymic: String,
    val about: String?,
)