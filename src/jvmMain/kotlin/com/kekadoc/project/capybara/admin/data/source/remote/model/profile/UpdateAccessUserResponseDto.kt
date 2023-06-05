package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessUserResponseDto(
    @SerialName("read_profile")
    val readProfile: Boolean,
    @SerialName("sent_notification")
    val sentNotification: Boolean,
    @SerialName("contact_info")
    val contactInfo: Boolean,
)