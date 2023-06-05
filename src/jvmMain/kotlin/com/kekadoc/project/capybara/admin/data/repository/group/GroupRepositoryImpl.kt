package com.kekadoc.project.capybara.admin.data.repository.group

import com.kekadoc.project.capybara.admin.data.source.remote.api.auth.AuthRemoteDataSource
import com.kekadoc.project.capybara.admin.data.source.remote.api.group.GroupRemoteDataSource
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GroupRepositoryImpl(
    private val remoteDataSource: GroupRemoteDataSource,
) : GroupRepository {

    override fun createGroup(name: String): Flow<GroupWithMembersIds> = flowOf {
        remoteDataSource.createGroup(name)
    }

    override fun getAllGroups(): Flow<List<GroupWithMembersIds>> = flowOf {
        remoteDataSource.getAllGroups()
    }

    override fun updateGroupName(id: Identifier, name: String): Flow<GroupWithMembersIds> = flowOf {
        remoteDataSource.updateGroupName(id = id, name = name)
    }

    override fun getGroups(ids: List<Identifier>): Flow<List<GroupWithMembersIds>> = flowOf {
        remoteDataSource.getGroups(ids)
    }

    override fun deleteGroup(id: Identifier): Flow<Unit> = flowOf {
        remoteDataSource.deleteGroup(id)
    }

    override fun addMembersToGroup(id: Identifier, members: List<Identifier>): Flow<GroupWithMembersIds> = flowOf {
        remoteDataSource.addMembersToGroup(id, members)
    }

    override fun removeMembersFromGroup(id: Identifier, members: List<Identifier>): Flow<GroupWithMembersIds> = flowOf {
        remoteDataSource.removeMembersFromGroup(id, members)
    }

}