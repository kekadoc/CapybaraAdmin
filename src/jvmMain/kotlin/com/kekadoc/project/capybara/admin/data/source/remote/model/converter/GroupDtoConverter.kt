package com.kekadoc.project.capybara.admin.data.source.remote.model.converter

import com.kekadoc.project.capybara.admin.data.source.remote.model.group.GroupWithMembersDto
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.server.common.converter.Converter

object GroupDtoConverter : Converter<GroupWithMembersDto, GroupWithMembersIds> {

    override fun convert(value: GroupWithMembersDto): GroupWithMembersIds = GroupWithMembersIds(
        id = value.id,
        name = value.name,
        members = value.membersIds,
    )

}