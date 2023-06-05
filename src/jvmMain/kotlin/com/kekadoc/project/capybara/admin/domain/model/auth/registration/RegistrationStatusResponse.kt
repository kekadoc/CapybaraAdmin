package com.kekadoc.project.capybara.admin.domain.model.auth.registration

import com.kekadoc.project.capybara.admin.domain.model.Identifier

data class RegistrationStatusResponse(
    val id: Identifier,
    val status: RegistrationStatus,
)