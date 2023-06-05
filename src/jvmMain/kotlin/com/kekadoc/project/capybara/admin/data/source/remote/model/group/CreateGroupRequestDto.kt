package com.kekadoc.project.capybara.admin.data.source.remote.model.group

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import kotlinx.serialization.Serializable

@Serializable
data class CreateGroupRequestDto(
    val name: String,
    val members: List<Identifier>,
)