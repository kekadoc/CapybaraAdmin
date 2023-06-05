package com.kekadoc.project.capybara.admin.data.source.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RangeDto(
    @SerialName("from")
    val from: Int,
    @SerialName("count")
    val count: Int,
    @SerialName("query")
    val query: String? = null
)