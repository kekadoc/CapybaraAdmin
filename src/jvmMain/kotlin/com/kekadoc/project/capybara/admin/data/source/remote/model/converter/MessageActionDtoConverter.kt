package com.kekadoc.project.capybara.admin.data.source.remote.model.converter

import com.kekadoc.project.capybara.server.common.converter.Converter
import com.kekadoc.project.capybara.admin.domain.model.message.MessageAction
import com.kekadoc.project.capybara.admin.data.source.remote.model.message.MessageActionDto

object MessageActionDtoConverter : Converter.Bidirectional<MessageAction, MessageActionDto> {

    override fun convert(value: MessageAction): MessageActionDto = MessageActionDto(
        id = value.id,
        text = value.text,
    )

    override fun revert(value: MessageActionDto): MessageAction = MessageAction(
        id = value.id,
        text = value.text,
    )

}