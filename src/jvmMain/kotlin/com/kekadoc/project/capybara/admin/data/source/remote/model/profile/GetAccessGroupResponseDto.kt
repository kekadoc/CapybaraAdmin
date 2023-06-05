package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAccessGroupResponseDto(
    @SerialName("read_info")
    val readInfo: Boolean,
    @SerialName("read_members")
    val readMembers: Boolean,
    @SerialName("sent_notification")
    val sentNotification: Boolean,
)