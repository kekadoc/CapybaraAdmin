package com.kekadoc.project.capybara.server.domain.model.auth.registration

import com.kekadoc.project.capybara.server.domain.model.Identifier

data class RegistrationRequest(
    val name: String,
    val surname: String,
    val patronymic: String,
    val email: String,
    val isStudent: Boolean,
    val groupId: Identifier?,
)