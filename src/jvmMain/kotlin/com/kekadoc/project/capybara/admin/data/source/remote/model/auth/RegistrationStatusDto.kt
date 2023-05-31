package com.kekadoc.project.capybara.admin.data.source.remote.model.auth

enum class RegistrationStatusDto {

    WAIT_EMAIL_CONFIRMING,
    WAIT_APPROVING,
    COMPLETED,
    REJECTED,
    CANCELLED,

}