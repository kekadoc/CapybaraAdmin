package com.kekadoc.project.capybara.admin.ui.resource.text

interface Text {

    val form: Form


    interface Form {

        val auth: Auth

        interface Auth {

            val loginLabel: String

            val passwordLabel: String

            val actionLogin: String

        }

    }

}