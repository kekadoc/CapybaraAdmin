package com.kekadoc.project.capybara.server.domain.model.auth.registration

import com.kekadoc.project.capybara.admin.domain.server.model.auth.registration.RegistrationRequestInfo

data class GetAllRegistrationRequestsResponse(
    val items: List<RegistrationRequestInfo>
)