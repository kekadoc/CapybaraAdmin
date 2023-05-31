package com.kekadoc.project.capybara.admin.data.repository.group

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.group.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {

    fun createGroup(name: String): Flow<Group>

    fun deleteGroup(id: Identifier): Flow<Unit>

    fun addMembersToGroup(members: List<Identifier>): Flow<Unit>

    fun removeMembersFromGroup(members: List<Identifier>): Flow<Unit>

}