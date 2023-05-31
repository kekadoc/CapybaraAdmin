package com.kekadoc.project.capybara.server.domain.model.message

enum class MessageStatus {
    UNDEFINED,
    /**
     * Принято сервером
     */
    RECEIVED,
    /**
     * Отправлено
     */
    SENT,
    /**
     * Отменено
     */
    CANCELED,
    /**
     * Заблокировано сервером
     */
    REJECTED,
    /**
     * Ошибка отправки
     */
    FAILED
}