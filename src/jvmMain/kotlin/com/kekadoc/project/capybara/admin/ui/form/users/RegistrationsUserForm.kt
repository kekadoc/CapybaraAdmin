package com.kekadoc.project.capybara.admin.ui.form.users

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.ui.form.Form

class RegistrationsUserViewState

class RegistrationsUserViewModel : ViewModel<RegistrationsUserViewState>(RegistrationsUserViewState())

@Composable
fun RegistrationsUserForm(viewModel: RegistrationsUserViewModel = viewModel()) = Form {
    val viewState by viewModel.container.stateFlow.collectAsState()
    Text("Registrations User")
}