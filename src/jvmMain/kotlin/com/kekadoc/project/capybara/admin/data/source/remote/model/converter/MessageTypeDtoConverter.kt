package com.kekadoc.project.capybara.server.routing.model.converter

import com.kekadoc.project.capybara.server.common.converter.Converter
import com.kekadoc.project.capybara.admin.domain.model.message.MessageType
import com.kekadoc.project.capybara.admin.data.source.remote.model.message.MessageTypeDto

object MessageTypeDtoConverter : Converter.Bidirectional<MessageType, MessageTypeDto> {

    override fun convert(value: MessageType): MessageTypeDto = when (value) {
        MessageType.INFO -> MessageTypeDto.INFO
        MessageType.VOTE -> MessageTypeDto.VOTE
        MessageType.DEFAULT -> MessageTypeDto.DEFAULT
    }

    override fun revert(value: MessageTypeDto): MessageType = when (value) {
        MessageTypeDto.INFO -> MessageType.INFO
        MessageTypeDto.VOTE -> MessageType.VOTE
        MessageTypeDto.DEFAULT -> MessageType.DEFAULT
    }

}