package com.kekadoc.project.capybara.admin.data.source.remote.model.converter

import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ProfileTypeDto
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.server.common.converter.Converter

object ProfileTypeDtoConverter : Converter.Bidirectional<ProfileTypeDto, ExtendedProfile.Type> {

    override fun convert(value: ProfileTypeDto): ExtendedProfile.Type = when (value) {
        ProfileTypeDto.USER -> ExtendedProfile.Type.USER
        ProfileTypeDto.SPEAKER -> ExtendedProfile.Type.SPEAKER
        ProfileTypeDto.ADMIN -> ExtendedProfile.Type.ADMIN
    }

    override fun revert(value: ExtendedProfile.Type): ProfileTypeDto = when (value) {
        ExtendedProfile.Type.USER -> ProfileTypeDto.USER
        ExtendedProfile.Type.SPEAKER -> ProfileTypeDto.SPEAKER
        ExtendedProfile.Type.ADMIN -> ProfileTypeDto.ADMIN
    }

}