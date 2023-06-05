package com.kekadoc.project.capybara.admin.data.repository.messages

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.message.SentMessagePreview
import com.kekadoc.project.capybara.admin.domain.model.message.MessageNotifications
import com.kekadoc.project.capybara.admin.domain.model.message.MessageType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MessagesRepository {

    fun getMessages(filter: MessagesFilter, range: Range): Flow<List<SentMessagePreview>>

    fun deleteMessage(messageId: Identifier): Flow<Unit>

    fun createMessage(
        type: MessageType,
        title: String?,
        text: String,
        actions: List<String>?,
        isMultiAnswer: Boolean,
        addresseeUsers: List<String>,
        addresseeGroups: Map<String, List<String>?>,
        notifications: MessageNotifications?
    ): Flow<Unit>

}

data class MessagesFilter(
    val authorId: Identifier? = null,
    val authorQuery: String? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val toGroupId: Identifier? = null,
    val toGroupQuery: Identifier? = null,
    val toUserId: Identifier? = null,
    val toUserQuery: Identifier? = null,
)