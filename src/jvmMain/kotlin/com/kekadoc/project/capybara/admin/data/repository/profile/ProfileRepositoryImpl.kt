package com.kekadoc.project.capybara.admin.data.repository.profile

import com.kekadoc.project.capybara.admin.data.source.remote.api.profile.ProfileRemoteDataSource
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToGroup
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToUser
import com.kekadoc.project.capybara.admin.domain.model.profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*

class ProfileRepositoryImpl(
    private val remoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {

    private val _currentProfile = MutableStateFlow<AuthorizedProfile?>(null)

    override val currentProfile: Flow<AuthorizedProfile?> = getProfile()
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.Lazily,
            initialValue = null,
        )
        .onEach { _currentProfile.emit(it) }
        .flatMapConcat { _currentProfile }

    override fun getProfile(): Flow<AuthorizedProfile> = flowOf {
        remoteDataSource.getProfile()
    }
        .onEach { _currentProfile.emit(it) }

    override fun getProfiles(ids: List<Identifier>): Flow<List<ShortProfile>> = flowOf {
        remoteDataSource.getProfiles(ids)
    }

    override fun getProfiles(range: Range): Flow<List<ExtendedProfile>> = flowOf {
        remoteDataSource.getProfiles(range)
    }

    override fun getUserAccessToUser(
        fromProfileId: Identifier,
        toProfileId: Identifier,
    ): Flow<UserAccessToUser> = flowOf {
        remoteDataSource.getUserAccessToUser(fromProfileId, toProfileId)
    }

    override fun getUserAccessToGroup(
        fromProfileId: Identifier,
        toGroupId: Identifier,
    ): Flow<UserAccessToGroup> = flowOf {
        remoteDataSource.getUserAccessToGroup(fromProfileId, toGroupId)
    }

    override fun updateProfilePersonal(
        profileId: Identifier,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
    ): Flow<ExtendedProfile> = flowOf {
        remoteDataSource.updateProfilePersonal(profileId, name, surname, patronymic, about)
    }

    override fun updateProfileType(
        profileId: Identifier,
        type: ExtendedProfile.Type,
    ): Flow<ExtendedProfile> = flowOf {
        remoteDataSource.updateProfileType(profileId, type)
    }

    override fun updateProfileStatus(
        profileId: Identifier,
        status: ExtendedProfile.Status,
    ): Flow<ExtendedProfile> = flowOf {
        remoteDataSource.updateProfileStatus(profileId, status)
    }

    override fun updateUserAccessToUser(
        fromUserId: Identifier,
        toUserId: Identifier,
        access: UserAccessToUser.Updater,
    ): Flow<UserAccessToUser> = flowOf {
        remoteDataSource.updateUserAccessToUser(fromUserId, toUserId, access)
    }

    override fun updateUserAccessToGroup(
        fromUserId: Identifier,
        toGroupId: Identifier,
        access: UserAccessToGroup.Updater,
    ): Flow<UserAccessToGroup> = flowOf {
        remoteDataSource.updateUserAccessToGroup(fromUserId, toGroupId, access)
    }

    override fun updateProfileCommunication(
        profileId: Identifier,
        communication: Communication,
    ): Flow<ExtendedProfile> = flowOf {
        remoteDataSource.updateProfileCommunication(profileId, communication)
    }

    override fun deleteProfile(profileId: Identifier): Flow<Unit> = flowOf {
        remoteDataSource.deleteProfile(profileId)
    }

    override fun createProfile(
        type: ExtendedProfile.Type,
        name: String,
        surname: String,
        patronymic: String,
        about: String?,
        emailForInvite: String?,
        login: String?,
        password: String?,
    ): Flow<CreateProfileResponse> = flowOf {
        remoteDataSource.createProfile(
            name = name,
            surname = surname,
            patronymic = patronymic,
            about = about,
            type = type,
            emailForInvite = emailForInvite,
            login = login,
            password = password,
        )
    }

}