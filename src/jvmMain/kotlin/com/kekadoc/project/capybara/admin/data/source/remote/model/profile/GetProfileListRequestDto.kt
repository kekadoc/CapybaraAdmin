package com.kekadoc.project.capybara.admin.data.source.remote.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetProfileListRequestDto(
    @SerialName("ids")
    val ids: List<String>,
)