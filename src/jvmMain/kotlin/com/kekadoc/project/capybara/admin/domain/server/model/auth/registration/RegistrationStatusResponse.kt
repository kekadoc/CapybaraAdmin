package com.kekadoc.project.capybara.server.domain.model.auth.registration

import com.kekadoc.project.capybara.server.domain.model.Identifier

data class RegistrationStatusResponse(
    val id: Identifier,
    val status: RegistrationStatus,
)