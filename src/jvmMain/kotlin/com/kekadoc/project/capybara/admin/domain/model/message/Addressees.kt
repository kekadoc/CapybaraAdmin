package com.kekadoc.project.capybara.domain.model.message

import com.kekadoc.project.capybara.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.domain.model.profile.Profile

data class Addressees(
    val users: List<Profile>,
    val groups: List<GroupWithMembersIds>,
)