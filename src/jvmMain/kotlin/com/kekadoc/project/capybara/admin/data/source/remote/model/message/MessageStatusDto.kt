package com.kekadoc.project.capybara.admin.data.source.remote.model.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MessageStatusDto {
    @SerialName("UNDEFINED")
    UNDEFINED,
    /**
     * Принято сервером
     */
    @SerialName("RECEIVED")
    RECEIVED,
    /**
     * Отправлено
     */
    @SerialName("SENT")
    SENT,
    /**
     * Отменено
     */
    @SerialName("CANCELED")
    CANCELED,
    /**
     * Заблокировано сервером
     */
    @SerialName("REJECTED")
    REJECTED,
    /**
     * Ошибка отправки
     */
    @SerialName("FAILED")
    FAILED
}