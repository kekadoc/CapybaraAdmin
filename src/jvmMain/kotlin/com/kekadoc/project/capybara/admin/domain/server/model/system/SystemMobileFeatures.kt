package com.kekadoc.project.capybara.server.domain.model.system

data class SystemMobileFeatures(
    val changeProfile: Boolean,
    val resetPassword: Boolean,
    val registration: Boolean,
)