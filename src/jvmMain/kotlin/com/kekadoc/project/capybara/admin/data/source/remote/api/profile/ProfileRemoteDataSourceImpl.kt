package com.kekadoc.project.capybara.admin.data.source.remote.api.profile

import com.kekadoc.project.capybara.admin.data.source.remote.api.bodyOrError
import com.kekadoc.project.capybara.admin.data.source.remote.model.RangeDto
import com.kekadoc.project.capybara.admin.data.source.remote.model.factory.ExtendedProfileDtoFactory
import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.*
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToGroup
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToUser
import com.kekadoc.project.capybara.admin.domain.model.profile.*
import io.ktor.client.*
import io.ktor.client.request.*

class ProfileRemoteDataSourceImpl(
    private val client: HttpClient,
) : ProfileRemoteDataSource {

    override suspend fun getProfile(): AuthorizedProfile {
        return client.get("profile")
            .bodyOrError<GetExtendedProfileResponseDto>()
            .profile
            .let(ExtendedProfileDtoFactory::create)
            .let(::AuthorizedProfile)
    }

    override suspend fun getProfiles(range: Range): List<ExtendedProfile> {
        return client.post("profile/list/full") {
            setBody(
                RangeDto(
                    from = range.from,
                    count = range.count,
                    query = range.query,
                )
            )
        }.bodyOrError<GetFullProfileListResponseDto>()
            .profiles
            .map(ExtendedProfileDtoFactory::create)
    }

    override suspend fun getProfiles(ids: List<Identifier>): List<ShortProfile> {
        return client.post("profile/list") {
            setBody(
                GetProfileListRequestDto(ids = ids)
            )
        }.bodyOrError<GetProfileListResponseDto>()
            .profiles
            .map {
                ShortProfile(
                    id = it.id,
                    name = it.name,
                    surname = it.surname,
                    patronymic = it.patronymic,
                    about = it.about,
                )
            }
    }

    override suspend fun updateProfilePersonal(
        profileId: Identifier,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
    ): ExtendedProfile {
        return client.patch("profile/$profileId/update/personal") {
            setBody(
                UpdateProfileRequestDto(
                    name = name,
                    surname = surname,
                    patronymic = patronymic,
                    about = about,
                )
            )
        }.bodyOrError<UpdateProfileResponseDto>()
            .profile
            .let(ExtendedProfileDtoFactory::create)
    }

    override suspend fun updateProfileType(
        profileId: Identifier,
        type: ExtendedProfile.Type,
    ): ExtendedProfile {
        return client.patch("profile/$profileId/update/type") {
            setBody(
                UpdateProfileTypeRequestDto(
                    type = ProfileTypeDto.valueOf(type.name)
                )
            )
        }.bodyOrError<UpdateProfileTypeResponseDto>()
            .profile
            .let(ExtendedProfileDtoFactory::create)
    }

    override suspend fun updateProfileStatus(
        profileId: Identifier,
        status: ExtendedProfile.Status,
    ): ExtendedProfile {
        return client.patch("profile/$profileId/update/status") {
            setBody(
                UpdateProfileStatusRequestDto(
                    status = status.name,
                )
            )
        }.bodyOrError<UpdateProfileStatusResponseDto>()
            .profile
            .let(ExtendedProfileDtoFactory::create)
    }

    override suspend fun updateUserAccessToUser(
        fromUserId: Identifier,
        toUserId: Identifier,
        access: UserAccessToUser.Updater,
    ): UserAccessToUser {
        val response = client.patch("profile/$fromUserId/access/user/$toUserId") {
            setBody(
                UpdateAccessUserRequestDto(
                    readProfile = access.readProfile,
                    sentNotification = access.sentNotification,
                    contactInfo = access.contactInfo,
                )
            )
        }.bodyOrError<UpdateAccessUserResponseDto>()
        return UserAccessToUser(
            fromUserId = fromUserId,
            toUserId = toUserId,
            readProfile = response.readProfile,
            contactInfo = response.contactInfo,
            sentNotification = response.sentNotification,
        )
    }

    override suspend fun getUserAccessToUser(
        fromProfileId: Identifier,
        toProfileId: Identifier,
    ): UserAccessToUser {
        return client.get("profile/$fromProfileId/access/user/$toProfileId")
            .bodyOrError<GetAccessUserResponseDto>()
            .let {
                UserAccessToUser(
                    fromUserId = fromProfileId,
                    toUserId = toProfileId,
                    readProfile = it.readProfile,
                    sentNotification = it.sentNotification,
                    contactInfo = it.contactInfo,
                )
            }
    }

    override suspend fun getUserAccessToGroup(
        fromProfileId: Identifier,
        toGroupId: Identifier,
    ): UserAccessToGroup {
        return client.get("profile/$fromProfileId/access/group/$toGroupId")
            .bodyOrError<GetAccessGroupResponseDto>()
            .let {
                UserAccessToGroup(
                    userId = fromProfileId,
                    groupId = toGroupId,
                    readInfo = it.readInfo,
                    readMembers = it.readMembers,
                    sentNotification = it.sentNotification,
                )
            }
    }

    override suspend fun updateUserAccessToGroup(
        userId: Identifier,
        groupId: Identifier,
        access: UserAccessToGroup.Updater,
    ): UserAccessToGroup {
        val response = client.patch("profile/$userId/access/group/$groupId") {
            setBody(
                UpdateAccessGroupRequestDto(
                    readInfo = access.readInfo,
                    readMembers = access.readMembers,
                    sentNotification = access.sentNotification,
                )
            )
        }.bodyOrError<UpdateAccessGroupResponseDto>()
        return UserAccessToGroup(
            userId = userId,
            groupId = groupId,
            readInfo = response.readInfo,
            readMembers = response.readMembers,
            sentNotification = response.sentNotification,
        )
    }

    override suspend fun updateProfileCommunication(
        profileId: Identifier,
        communication: Communication,
    ): ExtendedProfile {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProfile(profileId: Identifier) {
        client.delete("profile/$profileId")
    }

    override suspend fun createProfile(
        type: ExtendedProfile.Type,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
        emailForInvite: String?,
        login: String?,
        password: String?
    ): CreateProfileResponse {
        return client.post("profile") {
            setBody(
                CreateProfileRequestDto(
                    type = ProfileTypeDto.valueOf(type.name),
                    name = name,
                    surname = surname,
                    patronymic = patronymic,
                    about = about,
                    emailForInvite = emailForInvite,
                    login = login,
                    password = password,
                )
            )
        }.bodyOrError<CreateProfileResponseDto>()
            .let { (profile, pass) ->
                CreateProfileResponse(
                    profile = ExtendedProfileDtoFactory.create(profile),
                    tempPassword = pass,
                )
            }

    }
}