package com.kekadoc.project.capybara.admin.ui.resource.text.locales

import com.kekadoc.project.capybara.admin.ui.resource.text.Text

val RuText = object : Text {

    override val form: Text.Form = object : Text.Form {

        override val auth: Text.Form.Auth = object : Text.Form.Auth {

            override val loginLabel: String = "Логин"

            override val passwordLabel: String = "Пароль"

            override val actionLogin: String = "Войти"

        }

    }

}