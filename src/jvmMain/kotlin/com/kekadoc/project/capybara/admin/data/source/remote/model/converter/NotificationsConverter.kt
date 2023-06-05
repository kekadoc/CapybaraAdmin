package com.kekadoc.project.capybara.server.routing.model.converter

import com.kekadoc.project.capybara.server.common.converter.Converter
import com.kekadoc.project.capybara.admin.domain.model.message.MessageNotifications
import com.kekadoc.project.capybara.admin.data.source.remote.model.message.MessageNotificationsDto

object NotificationsConverter :
    Converter.Bidirectional<MessageNotifications, MessageNotificationsDto> {

    override fun convert(
        value: MessageNotifications,
    ): MessageNotificationsDto = MessageNotificationsDto(
        email = value.email,
        app = value.app,
        messengers = value.messengers,
    )

    override fun revert(
        value: MessageNotificationsDto,
    ): MessageNotifications = MessageNotifications(
        email = value.email,
        app = value.app,
        messengers = value.messengers,
    )

}