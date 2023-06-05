package com.kekadoc.project.capybara.admin.data.repository.group

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.group.Group
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import kotlinx.coroutines.flow.Flow

interface GroupRepository {

    fun createGroup(name: String): Flow<GroupWithMembersIds>

    fun getAllGroups(): Flow<List<GroupWithMembersIds>>

    fun getGroups(ids: List<Identifier>): Flow<List<GroupWithMembersIds>>

    fun updateGroupName(id: Identifier, name: String): Flow<GroupWithMembersIds>

    fun deleteGroup(id: Identifier): Flow<Unit>

    fun addMembersToGroup(id: Identifier, members: List<Identifier>): Flow<GroupWithMembersIds>

    fun removeMembersFromGroup(id: Identifier, members: List<Identifier>): Flow<GroupWithMembersIds>

}