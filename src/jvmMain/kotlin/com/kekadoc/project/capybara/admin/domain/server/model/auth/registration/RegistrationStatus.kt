package com.kekadoc.project.capybara.server.domain.model.auth.registration

enum class RegistrationStatus {

    WAIT_EMAIL_CONFIRMING,
    WAIT_APPROVING,
    REJECTED,
    CANCELLED,
    COMPLETED,

}