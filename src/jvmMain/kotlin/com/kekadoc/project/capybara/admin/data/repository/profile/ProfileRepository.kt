package com.kekadoc.project.capybara.admin.data.repository.profile

import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ProfileDto
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToGroup
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToUser
import com.kekadoc.project.capybara.admin.domain.model.profile.*
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    val currentProfile: Flow<AuthorizedProfile?>

    fun getProfile(): Flow<AuthorizedProfile>

    fun getProfiles(ids: List<Identifier>): Flow<List<ShortProfile>>

    fun getProfiles(range: Range): Flow<List<ExtendedProfile>>

    fun getUserAccessToUser(
        fromProfileId: Identifier,
        toProfileId: Identifier,
    ): Flow<UserAccessToUser>

    fun getUserAccessToGroup(
        fromProfileId: Identifier,
        toGroupId: Identifier,
    ): Flow<UserAccessToGroup>

    fun updateProfilePersonal(
        profileId: Identifier,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
    ): Flow<ExtendedProfile>

    fun updateProfileType(profileId: Identifier, type: ExtendedProfile.Type): Flow<ExtendedProfile>

    fun updateProfileStatus(profileId: Identifier, status: ExtendedProfile.Status): Flow<ExtendedProfile>

    fun updateUserAccessToUser(
        fromUserId: Identifier,
        toUserId: Identifier,
        access: UserAccessToUser.Updater,
    ): Flow<UserAccessToUser>

    fun updateUserAccessToGroup(
        fromUserId: Identifier,
        toGroupId: Identifier,
        access: UserAccessToGroup.Updater,
    ): Flow<UserAccessToGroup>

    fun updateProfileCommunication(profileId: Identifier, communication: Communication): Flow<ExtendedProfile>

    fun deleteProfile(profileId: Identifier): Flow<Unit>

    fun createProfile(
        type: ExtendedProfile.Type,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
        emailForInvite: String? = null,
        login: String? = null,
        password: String? = null,
    ): Flow<CreateProfileResponse>

}