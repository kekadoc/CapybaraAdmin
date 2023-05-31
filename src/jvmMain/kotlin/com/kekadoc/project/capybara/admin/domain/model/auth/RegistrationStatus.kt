package com.kekadoc.project.capybara.domain.model.auth

enum class RegistrationStatus {

    WAIT_EMAIL_CONFIRMING,
    WAIT_APPROVING,
    REJECTED,
    CANCELLED,
    COMPLETED,

}