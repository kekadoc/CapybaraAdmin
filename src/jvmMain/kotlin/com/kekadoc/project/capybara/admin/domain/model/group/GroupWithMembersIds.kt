package com.kekadoc.project.capybara.domain.model.group

import com.kekadoc.project.capybara.admin.domain.model.Identifier

data class GroupWithMembersIds(
    val id: Identifier,
    val name: String,
    val members: List<Identifier>,
)