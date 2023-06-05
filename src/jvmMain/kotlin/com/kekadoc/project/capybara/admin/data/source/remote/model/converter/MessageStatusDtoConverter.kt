package com.kekadoc.project.capybara.server.routing.model.converter

import com.kekadoc.project.capybara.server.common.converter.Converter
import com.kekadoc.project.capybara.admin.domain.model.message.MessageStatus
import com.kekadoc.project.capybara.admin.data.source.remote.model.message.MessageStatusDto

object MessageStatusDtoConverter : Converter.Bidirectional<MessageStatusDto, MessageStatus> {

    override fun convert(value: MessageStatusDto): MessageStatus = when (value) {
        MessageStatusDto.UNDEFINED -> MessageStatus.UNDEFINED
        MessageStatusDto.RECEIVED -> MessageStatus.RECEIVED
        MessageStatusDto.SENT -> MessageStatus.SENT
        MessageStatusDto.CANCELED -> MessageStatus.CANCELED
        MessageStatusDto.REJECTED -> MessageStatus.REJECTED
        MessageStatusDto.FAILED -> MessageStatus.FAILED
    }

    override fun revert(value: MessageStatus): MessageStatusDto = when (value) {
        MessageStatus.UNDEFINED -> MessageStatusDto.UNDEFINED
        MessageStatus.RECEIVED -> MessageStatusDto.RECEIVED
        MessageStatus.SENT -> MessageStatusDto.SENT
        MessageStatus.CANCELED -> MessageStatusDto.CANCELED
        MessageStatus.REJECTED -> MessageStatusDto.REJECTED
        MessageStatus.FAILED -> MessageStatusDto.FAILED
    }

}