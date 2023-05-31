package com.kekadoc.project.capybara.admin.data.repository.profile

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.profile.AuthorizedProfile
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.profile.Communication
import com.kekadoc.project.capybara.admin.domain.model.profile.Profile
import com.kekadoc.project.capybara.server.domain.model.UserAccessToGroup
import com.kekadoc.project.capybara.server.domain.model.UserAccessToUser
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun getProfile(): Flow<AuthorizedProfile>

    fun getProfiles(range: Range): Flow<Profile>

    fun updateProfilePersonal(
        profileId: Identifier,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
    ): Flow<Profile>

    fun updateProfileType(profileId: Identifier, type: Profile.Type): Flow<Profile>

    fun updateProfileStatus(profileId: Identifier, status: Profile.Status): Flow<Profile>

    fun updateProfileAccessToProfile(
        fromProfileId: Identifier,
        toProfileId: Identifier,
        access: UserAccessToUser.Updater,
    ): Flow<Profile>

    fun updateProfileAccessToGroup(
        fromProfileId: Identifier,
        toProfileId: Identifier,
        access: UserAccessToGroup.Updater,
    ): Flow<Profile>

    fun updateProfileCommunication(profileId: Identifier, communication: Communication): Flow<Profile>

    fun deleteProfile(profileId: Identifier): Flow<Unit>

    fun createProfile(
        login: String,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
    ): Flow<Profile>

}