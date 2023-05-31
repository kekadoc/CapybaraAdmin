package com.kekadoc.project.capybara.server.domain.model

data class Communication(
    val type: Type,
    val value: String,
    val approved: Boolean?,
) {

    companion object {

        operator fun invoke(
            type: String,
            value: String,
            approved: Boolean?,
        ): Communication = Communication(
            type = Type.from(type),
            value = value,
            approved = approved,
        )

    }

    sealed class Type(
        val name: String,
    ) {
        companion object {

            val values: List<Type> = listOf(
                Phone,
                Email,
                Viber,
                WhatsApp,
                Telegram,
            )

            fun from(name: String): Type = values.find { next -> next.name == name } ?: Unknown(name)

        }

        object Phone : Type("PHONE")
        object Email : Type("EMAIL")
        object Viber : Type("VIBER")
        object WhatsApp : Type("WHATS_APP")
        object Telegram : Type("TELEGRAM")
        class Unknown(code: String) : Type(code)

        override fun toString(): String = name

    }

}