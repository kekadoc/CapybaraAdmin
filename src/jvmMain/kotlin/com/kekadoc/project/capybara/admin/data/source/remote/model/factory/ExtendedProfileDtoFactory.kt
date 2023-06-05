package com.kekadoc.project.capybara.admin.data.source.remote.model.factory

import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ExtendedProfileDto
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.server.common.converter.revert
import com.kekadoc.project.capybara.server.common.factory.Factory
import com.kekadoc.project.capybara.admin.data.source.remote.model.converter.ProfileTypeDtoConverter
import com.kekadoc.project.capybara.admin.domain.model.profile.Communication

object ExtendedProfileDtoFactory : Factory.Single<ExtendedProfileDto, ExtendedProfile> {

    override fun create(value: ExtendedProfileDto): ExtendedProfile = ExtendedProfile(
        id = value.id,
        status = ExtendedProfile.Status.valueOf(value.status.name),
        login = value.login,
        type = ExtendedProfile.Type.valueOf(value.type.name),
        name = value.name,
        surname = value.surname,
        patronymic = value.patronymic,
        about = value.about,
        communications = value.communications.map {
            Communication(it.key, it.value.first, it.value.second)
        },
        groupIds = value.groupIds,
    )

}