package com.kekadoc.project.capybara.server.domain.model

/**
 * Что может делать пользователь с другим пользователем
 */
data class UserAccessToUser(
    val fromUserId: Identifier,
    val toUserId: Identifier,
    val readProfile: Boolean,
    val sentNotification: Boolean,
    val contactInfo: Boolean,
) {

    companion object {

        fun nothing(fromUserId: Identifier, toUserId: Identifier) = UserAccessToUser(
            fromUserId = fromUserId,
            toUserId = toUserId,
            readProfile = false,
            sentNotification = false,
            contactInfo = false,
        )

    }

    data class Updater(
        val readProfile: Boolean? = null,
        val sentNotification: Boolean? = null,
        val contactInfo: Boolean? = null,
    )

}