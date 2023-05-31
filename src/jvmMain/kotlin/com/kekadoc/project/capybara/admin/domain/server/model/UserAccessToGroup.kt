package com.kekadoc.project.capybara.server.domain.model

data class UserAccessToGroup(
    val userId: Identifier,
    val groupId: Identifier,
    val readInfo: Boolean,
    val readMembers: Boolean,
    val sentNotification: Boolean,
) {

    companion object {

        fun nothing(userId: Identifier, groupId: Identifier) = UserAccessToGroup(
            userId = userId,
            groupId = groupId,
            readInfo = false,
            readMembers = false,
            sentNotification = false,
        )

    }

    data class Updater(
        val readInfo: Boolean? = null,
        val readMembers: Boolean? = null,
        val sentNotification: Boolean? = null,
    )

}