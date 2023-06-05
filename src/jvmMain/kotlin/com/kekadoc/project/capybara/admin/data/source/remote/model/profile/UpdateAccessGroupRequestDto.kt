package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessGroupRequestDto(
    @SerialName("read_info")
    val readInfo: Boolean? = null,
    @SerialName("read_members")
    val readMembers: Boolean? = null,
    @SerialName("sent_notification")
    val sentNotification: Boolean? = null,
)