package com.kekadoc.project.capybara.admin.data.source.remote.api.group

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.group.Group
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import kotlinx.coroutines.flow.Flow

interface GroupRemoteDataSource {

    suspend fun createGroup(name: String): GroupWithMembersIds

    suspend fun getAllGroups(): List<GroupWithMembersIds>

    suspend fun getGroups(ids: List<Identifier>): List<GroupWithMembersIds>

    suspend fun updateGroupName(id: Identifier, name: String): GroupWithMembersIds

    suspend fun deleteGroup(id: Identifier)

    suspend fun addMembersToGroup(id: Identifier, members: List<Identifier>): GroupWithMembersIds

    suspend fun removeMembersFromGroup(id: Identifier, members: List<Identifier>): GroupWithMembersIds

}