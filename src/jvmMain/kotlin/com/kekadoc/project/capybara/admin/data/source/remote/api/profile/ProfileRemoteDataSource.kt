package com.kekadoc.project.capybara.admin.data.source.remote.api.profile

import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ProfileDto
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToGroup
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToUser
import com.kekadoc.project.capybara.admin.domain.model.profile.*
import kotlinx.coroutines.flow.Flow

interface ProfileRemoteDataSource {

    suspend fun getProfile(): AuthorizedProfile

    suspend fun getProfiles(range: Range): List<ExtendedProfile>

    suspend fun getProfiles(ids: List<Identifier>): List<ShortProfile>

    suspend fun updateProfilePersonal(
        profileId: Identifier,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
    ): ExtendedProfile

    suspend fun updateProfileType(
        profileId: Identifier,
        type: ExtendedProfile.Type,
    ): ExtendedProfile

    suspend fun updateProfileStatus(
        profileId: Identifier,
        status: ExtendedProfile.Status,
    ): ExtendedProfile

    suspend fun updateUserAccessToUser(
        fromUserId: Identifier,
        toUserId: Identifier,
        access: UserAccessToUser.Updater,
    ): UserAccessToUser

    suspend fun getUserAccessToUser(
        fromProfileId: Identifier,
        toProfileId: Identifier,
    ): UserAccessToUser

    suspend fun getUserAccessToGroup(
        fromProfileId: Identifier,
        toGroupId: Identifier,
    ): UserAccessToGroup

    suspend fun updateUserAccessToGroup(
        userId: Identifier,
        groupId: Identifier,
        access: UserAccessToGroup.Updater,
    ): UserAccessToGroup

    suspend fun updateProfileCommunication(
        profileId: Identifier,
        communication: Communication,
    ): ExtendedProfile

    suspend fun deleteProfile(profileId: Identifier)

    suspend fun createProfile(
        type: ExtendedProfile.Type,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
        emailForInvite: String? = null,
        login: String? = null,
        password: String? = null,
    ): CreateProfileResponse

}