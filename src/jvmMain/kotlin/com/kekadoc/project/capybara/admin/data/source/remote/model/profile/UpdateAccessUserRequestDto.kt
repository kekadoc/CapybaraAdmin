package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessUserRequestDto(
    @SerialName("read_profile")
    val readProfile: Boolean? = null,
    @SerialName("sent_notification")
    val sentNotification: Boolean? = null,
    @SerialName("contact_info")
    val contactInfo: Boolean? = null,
)