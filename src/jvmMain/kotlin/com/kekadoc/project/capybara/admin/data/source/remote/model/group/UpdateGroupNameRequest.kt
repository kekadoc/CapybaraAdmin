package com.kekadoc.project.capybara.admin.data.source.remote.model.group

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGroupNameRequest(
    val name: String,
)