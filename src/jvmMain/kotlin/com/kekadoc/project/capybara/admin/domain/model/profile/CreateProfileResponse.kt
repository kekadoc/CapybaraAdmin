package com.kekadoc.project.capybara.admin.domain.model.profile

data class CreateProfileResponse(
    val profile: ExtendedProfile,
    val tempPassword: String,
)