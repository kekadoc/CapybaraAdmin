package com.kekadoc.project.capybara.admin.data.source.remote.api.group

import com.kekadoc.project.capybara.admin.data.source.remote.api.bodyOrError
import com.kekadoc.project.capybara.admin.data.source.remote.model.group.*
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import io.ktor.client.*
import io.ktor.client.request.*

class GroupRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : GroupRemoteDataSource {

    override suspend fun createGroup(name: String): GroupWithMembersIds {
        return httpClient.post("groups") {
            setBody(CreateGroupRequestDto(name = name, members = emptyList()))
        }.bodyOrError<CreateGroupResponseDto>().let {
            GroupWithMembersIds(
                id = it.group.id,
                name = it.group.name,
                members = it.group.membersIds,
            )
        }
    }

    override suspend fun getAllGroups(): List<GroupWithMembersIds> {
        return httpClient.get("groups/all")
            .bodyOrError<GetAllGroupsWithMembersResponseDto>().let {
            it.items.map {
                GroupWithMembersIds(
                    id = it.id,
                    name = it.name,
                    members = it.membersIds,
                )
            }
        }
    }

    override suspend fun getGroups(ids: List<Identifier>): List<GroupWithMembersIds> {
        return httpClient.post("groups/list/extended") {
            setBody(GetGroupListRequestDto(ids = ids))
        }
            .bodyOrError<GetGroupWithMembersListResponseDto>().let {
                it.groups.map {
                    GroupWithMembersIds(
                        id = it.id,
                        name = it.name,
                        members = it.membersIds,
                    )
                }
            }
    }

    override suspend fun updateGroupName(id: Identifier, name: String): GroupWithMembersIds {
        return httpClient.post("groups/$id/name") {
            setBody(UpdateGroupNameRequest(name = name))
        }
            .bodyOrError<UpdateGroupResponseDto>().let { response ->
                response.group.let {
                    GroupWithMembersIds(
                        id = it.id,
                        name = it.name,
                        members = it.membersIds,
                    )
                }
            }
    }

    override suspend fun deleteGroup(id: Identifier) {
        httpClient.delete("groups/$id")
    }

    override suspend fun addMembersToGroup(id: Identifier, members: List<Identifier>): GroupWithMembersIds {
        return httpClient.patch("groups/$id/member/add"){
            setBody(UpdateGroupMembersRequest(members = members))
        }
            .bodyOrError<UpdateGroupResponseDto>()
            .let {
                GroupWithMembersIds(
                    id = it.group.id,
                    name = it.group.name,
                    members = it.group.membersIds,
                )
            }
    }

    override suspend fun removeMembersFromGroup(id: Identifier, members: List<Identifier>): GroupWithMembersIds {
        return httpClient.patch("groups/$id/member/delete"){
            setBody(UpdateGroupMembersRequest(members = members))
        }
            .bodyOrError<UpdateGroupResponseDto>()
            .let {
                GroupWithMembersIds(
                    id = it.group.id,
                    name = it.group.name,
                    members = it.group.membersIds,
                )
            }
    }
}